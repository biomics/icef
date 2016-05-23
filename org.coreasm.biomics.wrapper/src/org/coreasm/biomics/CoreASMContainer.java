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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.coreasm.biomics.serializers.EnumerationElementDeserializer;
import org.coreasm.biomics.serializers.EnumerationElementSerializer;
import org.coreasm.biomics.serializers.ListElementDeserializer;
import org.coreasm.biomics.serializers.ListElementSerializer;
import org.coreasm.biomics.serializers.MapElementDeserializer;
import org.coreasm.biomics.serializers.MapElementSerializer;
import org.coreasm.biomics.serializers.MessageElementSerializer;
import org.coreasm.biomics.serializers.NumberElementDeserializer;
import org.coreasm.biomics.serializers.NumberElementSerializer;
import org.coreasm.biomics.serializers.PolymorphicElement;
import org.coreasm.biomics.serializers.RuleElementDeserializer;
import org.coreasm.biomics.serializers.RuleElementSerializer;
import org.coreasm.biomics.serializers.SetElementDeserializer;
import org.coreasm.biomics.serializers.SetElementSerializer;
import org.coreasm.biomics.serializers.StringElementDeserializer;
import org.coreasm.biomics.serializers.StringElementSerializer; 
import org.coreasm.biomics.serializers.UpdateMultisetSerializer;
import org.coreasm.biomics.serializers.UpdateMultisetDeserializer;
import org.coreasm.biomics.serializers.LocationSerializer;
import org.coreasm.biomics.serializers.LocationDeserializer;
import org.coreasm.biomics.serializers.UpdateSerializer;
import org.coreasm.biomics.serializers.UpdateDeserializer;

import org.coreasm.engine.CoreASMEngine;
import org.coreasm.engine.CoreASMEngine.EngineMode;
import org.coreasm.engine.CoreASMEngineFactory;
import org.coreasm.engine.Engine;
import org.coreasm.engine.EngineProperties;
import org.coreasm.engine.InconsistentUpdateSetException;
import org.coreasm.engine.CoreASMError;

import org.coreasm.engine.parser.JParsecParser;

import org.coreasm.engine.absstorage.AgentCreationElement;
import org.coreasm.engine.absstorage.InvalidLocationException;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.NameElement;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.Update;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.absstorage.RuleElement;
import org.coreasm.engine.absstorage.UpdateMultiset;
import org.coreasm.engine.mailbox.Mailbox;
import org.coreasm.engine.plugins.list.ListElement;
import org.coreasm.engine.plugins.map.MapElement;
import org.coreasm.engine.plugins.number.NumberElement;
import org.coreasm.engine.plugins.set.SetElement;
import org.coreasm.engine.plugins.signature.EnumerationElement;
import org.coreasm.engine.plugins.string.StringElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CoreASMContainer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(CoreASMContainer.class);

    protected String simId;
    protected String asimName;
    protected String asimProgram;

	private CoreASMEngine engine = null;
	private UpdateMultiset lastUpdateSet = null;
    private ObjectMapper mapper = null;

    private HashSet<String> asimsToAdd = null;
    private HashSet<String> asimsToDel = null;
    private HashSet<MessageElement> inBox = null;
    private HashMap<String, HashSet<String>> updateRegistrations = null;
    private HashSet<String> requiredLocs = null;

    private boolean paused = false;

    public CoreASMContainer(ASIMCreationRequest req) {
        asimName = req.name;
        asimProgram = req.program;
    }

    public CoreASMContainer(String simulation, String newName, String newProgram) {
        asimName = newName;
        asimProgram = newProgram;
        simId = simulation;

        inBox = new HashSet<MessageElement>();
        asimsToAdd = new HashSet<String>();
        asimsToDel = new HashSet<String>();
        updateRegistrations = new HashMap<>();

        initEngine();

        if(!loadSpec(newProgram)) {
            System.err.println("[ASIM "+newName+"]: Error while loading BSL specification.");
            System.err.println("[ASIM "+newName+"]: "+getError());
        } else {
            System.out.println("[ASIM "+newName+"]: BSL specification successfully loaded.");
        }

        prepareMapper();
    }

    // TODO: Synchronize this!!!
    public void pauseASIM() {
        paused = true;
    }

    // TODO: Synchronize this!!!
    public void resumeASIM() {
        paused = false;
    }

    public boolean hasErrorOccurred() {
        return engine.hasErrorOccurred();
    }

    public CoreASMError getError() {
        return engine.getError();
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
            module.addSerializer(Location.class, new LocationSerializer());
            module.addDeserializer(Location.class, new LocationDeserializer());
            module.addSerializer(Update.class, new UpdateSerializer());
            module.addDeserializer(Update.class, new UpdateDeserializer());
            module.addSerializer(UpdateMultiset.class, new UpdateMultisetSerializer());
            module.addDeserializer(UpdateMultiset.class, new UpdateMultisetDeserializer());

            module.addSerializer(MessageElement.class, new MessageElementSerializer());
            mapper.registerModule(module);
        
            mapper.addMixInAnnotations(Element.class, PolymorphicElement.class);
        }
    }

    public void handleOutgoingMessages() {
        Set<MessageElement> messages = engine.emptyOutbox();

        Iterator<MessageElement> it = messages.iterator();
        Set<MessageElement> toSend = new HashSet<>();

        // System.out.println("[Thread "+getName()+"]: ASIM "+asimName+" handleOutgoingMessages() - messages: "+messages.size());

        // 1. check all toAgent addresses
        //  + if address has format self, replace it by asim name and put in inbox directly
        //  + if address has format NAME@NAME or @NAME and NAME is name of this ASIM, remove @NAME and put in inbox directly
        //  + if address has format NAME, check for NAME in local agents, if found, deliver directly
        //    otherwise, transform address into NAME@NAME
        //  + if address has format XYZ@NAME and NAME is not this ASIM, do nothing 

        // 2. for addresses in fromAgent
        //  + if address has format self, replace it by asim name
        //  + if address has format NAME, add the name of this ASIM in the form NAME@ASIM
        //  + if address has format NAME@XYZ, replace it by NAME@ASIM
        //  - if address has format @XYZ or @ASIM, replace if by ASIM@ASIM

        Set<? extends Element> a = null;
        a = engine.getAgentSet();

        if(a == null)
            return;

        HashSet<String> agents = new HashSet<>();
        for(Element e : a)
            agents.add(e.toString());

        int counter = 0;
        while(it.hasNext()) {
            counter++;
            MessageElement msg = new MessageElement(it.next());

            // System.out.println(counter + ": [Thread "+getName()+", size: "+messages.size()+"]: CoreASMContainer.handleOutgoingMessages: "+msg);

            String toAgent = msg.getToAgent();
            
            if(toAgent.equals("self")) {
                msg.setToAgent(asimName);
                fillInBox(msg);
                continue;
            }

            int index = -1;
            if(agents.contains(toAgent)) {
                fillInBox(msg);
                continue;
            } else {
                index = toAgent.indexOf("@");

                if(index == -1)
                    msg.setToAgent(toAgent + "@" + toAgent);
                else {
                    if(toAgent.substring(index+1).equals(asimName)) {
                        if(index == 0) {
                            msg.setToAgent(asimName);
                            fillInBox(msg);
                            continue;
                        } else {
                            msg.setToAgent(toAgent.substring(0, index));
                            fillInBox(msg);
                            continue;
                        }
                    }
                }
            }
            
            String fromAgent = msg.getFromAgent();
            index = fromAgent.indexOf("@");

            if(fromAgent.equals("self")) {
                msg.setFromAgent(asimName + "@" + asimName);
            } else {
                if(index == -1) {
                    msg.setFromAgent(fromAgent + "@" + asimName);
                } else {
                    if(index == 0)
                        msg.setFromAgent(asimName + "@" + asimName);
                    else {
                        String suffix = fromAgent.substring(index + 1);
                        if(!suffix.equals(asimName)) {
                            if(index > 0)
                                msg.setFromAgent(fromAgent.substring(0, index + 1) + asimName);
                        }
                    }
                }
            }

            toSend.add(msg);
        }
        
        MessageElement m = new MessageElement();
        String json = "";

        it = toSend.iterator();
        while(it.hasNext()) {
            MessageElement msg = it.next();

            try {
                json = mapper.writeValueAsString(msg);

                MessageRequest req = new MessageRequest("msg", simId, msg.getFromAgent(), msg.getToAgent(), json);
                EngineManager.sendMsg(req);

            } catch (Exception e) {
                System.err.println("Unable to transform MessageElement into json.");
                System.err.println(e);
                e.printStackTrace();
            }

            
            /*System.out.println("\t----------------------------------");
              System.out.println("\tMsg: "+msg);
              System.out.println("\tJSON: "+json);
              System.out.println("\t----------------------------------");*/
            
        }
    }

    public Map<String, UpdateMultiset> prepareUpdates(UpdateMultiset updates) {
        HashMap<String, UpdateMultiset> map = new HashMap<>();

        Iterator<Update> it = updates.iterator();
        while(it.hasNext()) {
            Update update = it.next();
            if(updateRegistrations.containsKey(update.loc.name)) {
                Set<String> targets = updateRegistrations.get(update.loc.name);
                for(String t : targets) {
                    // System.out.println("=> Send "+update.loc.name+" to "+t);
                    if(!map.containsKey(t))
                        map.put(t, new UpdateMultiset());
                    map.get(t).add(update);
                }
            }
        }

        return map;
    }

    public void distributeUpdateSet(UpdateMultiset updates) {
        // System.out.println("+++ handleUpdateSet +++ ");

        Map<String, UpdateMultiset> toSend = prepareUpdates(updates);
        Set<String> targets = toSend.keySet();

        String json = "";
        for(String target : targets) {
            try {
                json = mapper.writeValueAsString(toSend.get(target));
                MessageRequest req = new MessageRequest("update", simId, asimName, target, json);
                EngineManager.sendUpdate(simId, req);
            } catch (Exception e) {
                System.err.println("Unable to transform UpdateSet into json.");
                System.err.println(e);
                e.printStackTrace();
            }
        }
        
        // System.out.println("--- handleUpdateSet --- ");
    }

    public synchronized void newASIM(String name) {
        asimsToAdd.add(name);
    }

    public synchronized void delASIM(String name) {
        asimsToDel.add(name);
    }

    public synchronized void injectASIMs() {
        for(String asim : asimsToAdd) 
            System.out.println("***** CoreASMContainer: Adding ASIM: "+asim+" *****");
        for(String asim : asimsToDel) 
            System.out.println("***** CoreASMContainer: Deleting ASIM: "+asim+" *****");
        engine.addASIMs(asimsToAdd);
        engine.deleteASIMs(asimsToDel);

        asimsToDel.clear();
        asimsToAdd.clear();
    }

    // TODO: NEEDS TO BE SYNCHRONIZED!!!
    public boolean injectUpdates() {
        /* engine.updateState(updateSet);
           updateSet.clear(); */

        return true;
    }

    // TODO: NEEDS TO BE SYNCHRONIZED!!!
    public boolean receiveUpdate(MessageRequest req) {
        // System.out.println("CoreASMContainer receives update");

        String strUpdates = req.body;
        // System.out.println("strUpdates: "+strUpdates);

        UpdateMultiset updates = null;
        try {
            updates = mapper.readValue(strUpdates, UpdateMultiset.class);
        } catch (IOException ioe) {
            System.err.println("Unable to transform JSON '"+strUpdates+"' into UpdateMultiset.");
            System.err.println(ioe);
        }
        // updateSet.add(updates);
        Set<Update> updateSet = new HashSet<>();
        Iterator<Update> it = updates.iterator();

        // introduce a scope
        for(Update u : updates) {
            // u.loc.args.add(0, new StringElement(req.fromAgent));
            List<Element> newArgs = new ArrayList<>();
            newArgs.add(new StringElement(req.fromAgent));
            newArgs.addAll(u.loc.args);
            Location newLoc = new Location(u.loc.name, newArgs);
            Update newUpdate = new Update(newLoc, u.value, u.action, (Element)null, null);
            // System.out.println(">>> "+newUpdate.toString()+" <<<");
            updateSet.add(newUpdate);
        }

        try {
            engine.updateState(updateSet);
        } 
        catch(InconsistentUpdateSetException incUpdate) {
            System.err.println("Refuse update as it is inconsistent.");
        } 
        catch(InvalidLocationException invalidLoc) {
            System.err.println("Refuse update as a location is invalid.");
        }

        return true;
    }

    public synchronized boolean register4Update(String target, String location) {
        if(!updateRegistrations.containsKey(location))
            updateRegistrations.put(location, new HashSet<String>());
        
        updateRegistrations.get(location).add(target);
        
        return true;
    }

    public void run() {
        int currentStep = 1;

        do {

            if(engine == null || engine.getEngineMode().equals(EngineMode.emTerminated)) {
                System.out.println("ASIM "+asimName+" terminates");
                engine = null;
                break;
            }

            // ugh ... how ugly but the way coreASM works, this is needed
            try {
                Thread.sleep(100);
                if(paused) {
                    Thread.sleep(500);
                    continue;
                }
            } catch (InterruptedException ie) {
                // TODO REPORT STH HERE
            }

			if (currentStep == 1)
				lastUpdateSet = new UpdateMultiset();
			else
				lastUpdateSet = new UpdateMultiset(engine.getUpdateSet(0));

            distributeUpdateSet(lastUpdateSet);

            injectUpdates();
            injectASIMs();
            
            if(getInBoxSize() > 0) {
                engine.fillInBox(getInBox());
                emptyInBox();
            }

			engine.step();
			engine.waitWhileBusyOrUntilCreation();

            if(engine.getEngineMode() == EngineMode.emCreateAgent) {
                Map<String, AgentCreationElement> loc2Agent = engine.getAgentsToCreate();
                Set<String> locs = loc2Agent.keySet();
                Iterator<String> it = locs.iterator();

                HashMap<String, String> agents = new HashMap<String,String>();
                it = locs.iterator();
                int counter = 1;
                while(it.hasNext()) {
                    String loc = it.next();
                    String name = EngineManager.requestASIMCreation(loc2Agent.get(loc), simId);
                    agents.put(loc, name);
                }
                engine.reportNewAgents(agents);
            }
            engine.waitWhileBusy();

			if (engine.getEngineMode() == EngineMode.emError) {
                System.err.println("[ASIM Execution ERROR]: "+asimName+": "+engine.getError());
            }

            // System.out.println("handle outgoing Messages");

            handleOutgoingMessages();

            deleteASIMs();
                
            /* System.out.println(" + ----- end of STEP " + currentStep + " ----- + \n");                
            System.out.println("\tUpdates after step " + currentStep + " are : " + engine.getUpdateSet(0));
            System.out.println();*/

			currentStep++;
            
		} while(true);
    }

    public void deleteASIMs() {
        System.out.println("deleteASIMs: "+engine.getAgentsToDelete().size());
        for(String s : engine.getAgentsToDelete())
            System.out.println("Delete "+s);

        EngineManager.requestASIMDeletion(simId, engine.getAgentsToDelete());
    }

    public boolean receiveMsg(MessageRequest req) {
        String agentMsg = req.body;

        MessageElement newMsg = null;
        try {
            newMsg = mapper.readValue(agentMsg, MessageElement.class);
        } catch (IOException ioe) {
            System.err.println("Unable to transform JSON '"+agentMsg+"' into MessageElement.");
            System.err.println(ioe);
        }
        
        String toAgent = newMsg.getToAgent();
        int index = toAgent.indexOf("@"+asimName);
        if(index == -1) {
            System.err.println("WARNING: Message not determined for this ASIM. Ignored");
            return false;
        } else
            newMsg.setToAgent(toAgent.substring(0, index));

        fillInBox(newMsg);

        return true;
    }

    private synchronized Set<MessageElement> getInBox() {
        return inBox;
    }

    private synchronized void fillInBox(MessageElement newMsg) {
        inBox.add(newMsg);
    }

    private synchronized void emptyInBox() {
        inBox.clear();
    }

    private synchronized int getInBoxSize() {
        return inBox.size();
    }

    public String getAsimName() {
        return asimName;
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

            engine.setSelfName(asimName);

            if(engine.getEngineMode() == EngineMode.emError)
                return false;
            else {
                requiredLocs = new HashSet<>(((JParsecParser)engine.getParser()).getRequiredLocations());
                EngineManager.registerLocations(asimName, simId, requiredLocs);
                return true;
            }
        } else 
            return false;
    }

    public void destroy() {
        if(engine != null) {
            engine.terminate();
            engine.waitWhileBusy();
        }
    }
}
