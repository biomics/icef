/*	
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Partially reuses Carma.java in org.coreasm.ui with  
 * Copyright (C) 2006-2010 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */

package org.coreasm.biomics;

import java.io.StringReader;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.coreasm.engine.Engine;
import org.coreasm.engine.CoreASMEngine;
import org.coreasm.engine.CoreASMEngine.EngineMode;
import org.coreasm.engine.CoreASMEngineFactory;
import org.coreasm.engine.CoreASMError;
import org.coreasm.engine.CoreASMWarning;
import org.coreasm.engine.EngineErrorEvent;
import org.coreasm.engine.EngineErrorObserver;
import org.coreasm.engine.EngineEvent;
import org.coreasm.engine.EngineProperties;
import org.coreasm.engine.EngineStepObserver;
import org.coreasm.engine.Specification;
import org.coreasm.engine.Specification.BackgroundInfo;
import org.coreasm.engine.Specification.FunctionInfo;
import org.coreasm.engine.Specification.UniverseInfo;
import org.coreasm.engine.StepFailedEvent;
import org.coreasm.engine.VersionInfo;
import org.coreasm.engine.VersionInfoProvider;
import org.coreasm.engine.absstorage.UpdateMultiset;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.interpreter.Node;
import org.coreasm.engine.plugin.PluginServiceInterface;
import org.coreasm.engine.plugins.io.IOPlugin.IOPluginPSI;
import org.coreasm.engine.plugins.io.InputProvider;
import org.coreasm.engine.absstorage.State;
import org.coreasm.engine.absstorage.AbstractUniverse;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.mailbox.Mailbox;
import org.coreasm.latex.CoreLaTeX;
import org.coreasm.util.Tools;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.RuleElement;
import org.coreasm.engine.plugins.string.StringElement;
import org.coreasm.engine.plugins.number.NumberElement;
import org.coreasm.engine.plugins.set.SetElement;
import org.coreasm.engine.plugins.list.ListElement;
import org.coreasm.engine.plugins.map.MapElement;
import org.coreasm.engine.plugins.signature.EnumerationElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.Version;

public class CoreASMContainer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(CoreASMContainer.class);

    protected String agentName;
    protected String agentProgram;

	private CoreASMEngine engine = null;
	private UpdateMultiset lastUpdateSet = null;
    private ObjectMapper mapper = null;

    private HashSet<MessageElement> inBox = null;

    public CoreASMContainer(AgentCreationRequest req) {
        agentName = req.name;
        agentProgram = req.program;
    }

    public CoreASMContainer(String newName, String newProgram) {
        agentName = newName;
        agentProgram = newProgram;

        inBox = new HashSet<MessageElement>();

        initEngine();

        if(!loadSpec(newProgram)) {
            System.err.println("Error while loading program '"+newProgram+"'");
        } else {
            System.out.println("Programm successfully loaded");
        }

        prepareMapper();
    }

    private void prepareMapper() {
        if(mapper == null) {
            mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule("Element Serializer", new Version(0,1,1,"FINAL"));
            module.addSerializer(StringElement.class, new StringElementSerializer());
            module.addDeserializer(StringElement.class, new StringElementDeserializer());
            module.addSerializer(NumberElement.class, new NumberElementSerializer());
            module.addDeserializer(NumberElement.class, new NumberElementDeserializer());
            module.addSerializer(SetElement.class, new SetElementSerializer());
            module.addDeserializer(SetElement.class, new SetElementDeserializer());
            module.addSerializer(ListElement.class, new ListElementSerializer());
            module.addDeserializer(ListElement.class, new ListElementDeserializer());
            module.addSerializer(MapElement.class, new MapElementSerializer());
            module.addDeserializer(MapElement.class, new MapElementDeserializer());
            module.addSerializer(EnumerationElement.class, new EnumerationElementSerializer());
            module.addDeserializer(EnumerationElement.class, new EnumerationElementDeserializer());
            module.addSerializer(RuleElement.class, new RuleElementSerializer());
            module.addDeserializer(RuleElement.class, new RuleElementDeserializer((Engine)engine));

            module.addSerializer(MessageElement.class, new MessageElementSerializer());
            mapper.registerModule(module);
        
            mapper.addMixInAnnotations(Element.class, PolymorphicElement.class);
        }
    }

    public void handleOutgoingMessages() {
        Set<MessageElement> messages = engine.getMailbox().emptyOutbox();
        Iterator<MessageElement> it = messages.iterator();

        while(it.hasNext()) {
            MessageElement msg = it.next();
            if(msg.getFromAgent().equals("self")) {
                msg.setFromAgent(agentName);
            } else {
                msg.setFromAgent(agentName + ":" + msg.getFromAgent());
            }
        }
        
        MessageElement m = new MessageElement();
        String json = "";

        it = messages.iterator();
        while(it.hasNext()) {
            MessageElement msg = it.next();

            try {
                json = mapper.writeValueAsString(msg);

                MessageRequest req = new MessageRequest("agent", msg.getFromAgent(), msg.getToAgent(), json);
                EngineManager.sendMsg(req);

            } catch (Exception e) {
                System.err.println("Unable to transform MessageElement into json.");
                System.err.println(e);
                e.printStackTrace();
                
                System.out.println("----------------------------------");
                System.out.println("Error Msg: "+msg);
                System.out.println("Error JSON: "+json);
                System.out.println("----------------------------------");
            }

            /*
              System.out.println("\t----------------------------------");
              System.out.println("\tMsg: "+msg);
              System.out.println("\tJSON: "+json);
              System.out.println("\t----------------------------------");
            */
        }
    }

    public void run() {
        int currentStep = 1;

        Mailbox mailbox = null;

        Set<MessageElement> messages = null;

        do {
            // System.out.println(" + ----- start of STEP " + currentStep + " ----- + \n");
            
			if (currentStep == 1)
				lastUpdateSet = new UpdateMultiset();
			else
				lastUpdateSet = new UpdateMultiset(engine.getUpdateSet(0));

            if(inBox.size() > 0) {
                engine.getMailbox().fillInbox(inBox);
                inBox.clear();
            }

			engine.step();
			engine.waitWhileBusyOrUntilCreation();

            if(engine.getEngineMode() == EngineMode.emCreateAgent) {
                System.out.println("Engine waits for creation of agents");
                Map<String, String> loc2Agent = engine.getAgentsToCreate();
                Set<String> locs = loc2Agent.keySet();
                Iterator<String> it = locs.iterator();
                while(it.hasNext()) {
                    String loc = it.next();
                    System.out.println("\tLoc: "+loc+"; name: "+loc2Agent.get(loc));
                }

                HashMap<String,String> agents = new HashMap<String,String>();
                it = locs.iterator();
                int counter = 1;
                while(it.hasNext()) {
                    String loc = it.next();
                    if(loc2Agent.get(loc).equals("")) {
                        counter++;
                        agents.put(loc, "Agent"+counter);
                    }
                }
                engine.reportNewAgents(agents);
            }
            engine.waitWhileBusy();
			
			if (engine.getEngineMode() == EngineMode.emError) {
                System.err.println("THERE WAS AN ERROR DURING EXECUTION");
            }

            // System.out.println("handle outgoing Messages");

            handleOutgoingMessages();
                
            /* System.out.println(" + ----- end of STEP " + currentStep + " ----- + \n");                
            System.out.println("\tUpdates after step " + currentStep + " are : " + engine.getUpdateSet(0));
            System.out.println();*/

			currentStep++;

            // ugh ... how ugly but the way coreASM works, this is needed
            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                // TODO REPORT STH HERE
            }
            
		} while(true || engine.getEngineMode().equals(EngineMode.emTerminated));
    }

    public void receiveMsg(MessageRequest req) {
        String agentMsg = req.body;

        // System.out.println("CoreASM receiveMsg");

        MessageElement newMsg = null;
        try {
            newMsg = mapper.readValue(agentMsg, MessageElement.class);
        } catch (IOException ioe) {
            System.err.println("Unable to transform JSON '"+agentMsg+"' into MessageElement.");
            System.err.println(ioe);
        }

        // System.out.println("Put new msg into CoreASM instance inBox");
        // System.out.println("MSG: "+newMsg);
        
        inBox.add(newMsg);
    }

    private void initEngine() {
        CoreASMEngine tempEngine = CoreASMEngineFactory.createEngine();

        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger) {
        	ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)root;
            rootLogger.setLevel(ch.qos.logback.classic.Level.ERROR);
        } else {
        	logger.warn("Could not turn of logging.");
        }

        tempEngine.setProperty(EngineProperties.MAX_PROCESSORS, String.valueOf(1));
        tempEngine.setProperty(EngineProperties.AGENT_EXECUTION_THREAD_BATCH_SIZE, String.valueOf(1));

		// tempEngine.addObserver(this); 
        tempEngine.initialize();
		tempEngine.waitWhileBusy();

        synchronized(this) {
            engine = tempEngine;
        }
    }

    private boolean loadSpec(String prog) {
        if(engine != null) {
            engine.loadSpecification(new StringReader(prog));
            engine.waitWhileBusy();
            return true;
        } else 
            return false;
    }

    public void destroy() {
        if(engine != null) {
            engine.terminate();
            engine.waitWhileBusy();
            engine = null;
        }
    }
}
