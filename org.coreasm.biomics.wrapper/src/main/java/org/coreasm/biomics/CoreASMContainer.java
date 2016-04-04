/*	
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Uses Fractions from Carma.java in org.coreasm.ui with  
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
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class CoreASMContainer {
    private static final Logger logger = LoggerFactory.getLogger(CoreASMContainer.class);

    protected String agentName;
    protected String agentProgram;

	private CoreASMEngine engine = null;
	private UpdateMultiset lastUpdateSet = null;

    public CoreASMContainer(AgentCreationRequest req) {
        agentName = req.name;
        agentProgram = req.program;
    }

    public CoreASMContainer(String newName, String newProgram) {
        agentName = newName;
        agentProgram = newProgram;

        initEngine();

        if(!loadSpec(newProgram)) {
            System.err.println("Error while loading program '"+newProgram+"'");
        } else {
            System.out.println("Programm successfully loaded");
        }
    }

    public void exec() {
        int currentStep = 1;

        Mailbox mailbox = null;

        Set<MessageElement> messages = null;

        do {
            System.out.println(" + ----- start of STEP " + currentStep + " ----- + \n");
            
			if (currentStep == 1)
				lastUpdateSet = new UpdateMultiset();
			else
				lastUpdateSet = new UpdateMultiset(engine.getUpdateSet(0));

            if(messages != null) {
                engine.getMailbox().fillInbox(messages);
            }

			engine.step();
			// engine.waitWhileBusy();
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
			
			if (engine.getEngineMode() == EngineMode.emError)
                System.err.println("THERE WAS AN ERROR DURING EXECUTION");

            // error(engine);

			/* if (updateFailed)
               break;*/

            /* State s = engine.getState();

            System.out.println("CURRENT STATE: "+s);

            Set<Location> locations = s.getLocations();
            Map<String, AbstractUniverse>universes = s.getUniverses();

            System.out.println("Universes: "+universes); */

            messages = engine.getMailbox().emptyOutbox();
            Iterator<MessageElement> it = messages.iterator();
            while(it.hasNext()) {
                MessageElement msg = it.next();
                if(msg.getFromAgent().equals("self")) {
                    msg.setFromAgent(agentName);
                } else {
                    msg.setFromAgent(agentName + ":" + msg.getFromAgent());
                }
            }
            
            System.out.println("Messages to send: " + messages.size());

            System.out.println(" + ----- end of STEP " + currentStep + " ----- + \n");
            
            System.out.println("\tUpdates after step " + currentStep + " are : " + engine.getUpdateSet(0));
			currentStep++;
		} while(true || engine.getEngineMode().equals(EngineMode.emTerminated));
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
