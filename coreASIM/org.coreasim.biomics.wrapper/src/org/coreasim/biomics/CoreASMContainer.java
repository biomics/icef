/*
 * CoreASMContainer.java v1.0
 *
 * This file contains source code developed by the European
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Partially reuses Carma.java in org.coreasm.ui with
 * Copyright (C) 2006-2010 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */

package org.coreasim.biomics;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.coreasim.biomics.serializers.EnumerationElementDeserializer;
import org.coreasim.biomics.serializers.EnumerationElementSerializer;
import org.coreasim.biomics.serializers.ListElementDeserializer;
import org.coreasim.biomics.serializers.ListElementSerializer;
import org.coreasim.biomics.serializers.LocationDeserializer;
import org.coreasim.biomics.serializers.LocationSerializer;
import org.coreasim.biomics.serializers.MapElementDeserializer;
import org.coreasim.biomics.serializers.MapElementSerializer;
import org.coreasim.biomics.serializers.MessageElementSerializer;
import org.coreasim.biomics.serializers.NumberElementDeserializer;
import org.coreasim.biomics.serializers.NumberElementSerializer;
import org.coreasim.biomics.serializers.PolymorphicElement;
import org.coreasim.biomics.serializers.RuleElementDeserializer;
import org.coreasim.biomics.serializers.RuleElementSerializer;
import org.coreasim.biomics.serializers.SetElementDeserializer;
import org.coreasim.biomics.serializers.SetElementSerializer;
import org.coreasim.biomics.serializers.StringElementDeserializer;
import org.coreasim.biomics.serializers.StringElementSerializer;
import org.coreasim.biomics.serializers.UpdateDeserializer;
import org.coreasim.biomics.serializers.UpdateMultisetDeserializer;
import org.coreasim.biomics.serializers.UpdateMultisetSerializer;
import org.coreasim.biomics.serializers.UpdateSerializer;
import org.coreasim.engine.CoreASIMEngine;
import org.coreasim.engine.CoreASIMEngineFactory;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.Engine;
import org.coreasim.engine.EngineProperties;
import org.coreasim.engine.InconsistentUpdateSetException;
import org.coreasim.engine.CoreASIMEngine.EngineMode;
import org.coreasim.engine.absstorage.AgentCreationElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.InvalidLocationException;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.absstorage.NameElement;
import org.coreasim.engine.absstorage.RuleElement;
import org.coreasim.engine.absstorage.Update;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.mailbox.Mailbox;
import org.coreasim.engine.parser.JParsecParser;
import org.coreasim.engine.plugins.list.ListElement;
import org.coreasim.engine.plugins.map.MapElement;
import org.coreasim.engine.plugins.number.NumberElement;
import org.coreasim.engine.plugins.set.SetElement;
import org.coreasim.engine.plugins.signature.EnumerationElement;
import org.coreasim.engine.plugins.string.StringElement;
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
    protected int delay;

    private CoreASIMEngine engine = null;
    private UpdateMultiset lastUpdateSet = null;
    private ObjectMapper mapper = null;

    private HashMap<Location, Update> updateMap = null;
    private HashSet<String> asimsToAdd = null;
    private HashSet<String> asimsToDel = null;
    private HashSet<MessageElement> inBox = null;
    private HashMap<String, HashSet<String>> updateRegistrations = null;
    private HashSet<String> requiredLocs = null;

    private boolean running = false;
    private boolean stopped = false;
    private boolean paused = false;
    private boolean inError = false;
    private CoreASIMError parseError = null;

    public CoreASMContainer(ASIMCreationRequest req) {
        asimName = req.name;
        asimProgram = req.program;
    }

    public CoreASMContainer(String simulation, String newName, String newProgram, int rate) {
        asimName = newName;
        asimProgram = newProgram;
        simId = simulation;

        inBox = new HashSet<MessageElement>();
        asimsToAdd = new HashSet<String>();
        asimsToDel = new HashSet<String>();
        updateMap = new HashMap<Location, Update>();
        updateRegistrations = new HashMap<>();

        inError = false;
        parseError = null;

        delay = rate;
        delay = 100;

        initEngine();

        if(!loadSpec(newProgram)) {
            System.err.println("[ASIM "+newName+"]: Error while loading BSL specification.");
            System.err.println("[ASIM "+newName+"]: "+getError());
        } else {
            System.out.println("[ASIM "+newName+"]: BSL specification successfully loaded.");
        }

        prepareMapper();
    }

    public boolean isRunning() {
        return running;
    }

    public String getStatus() {
	if(inError)
	    return "error";
	else
	    if(stopped)
		return "stopped";
	    else
		if(running)
		    return "running";
		else
		    if(paused)
			return "paused";
		    else
			return "idle";
    }

    public String toJSON() {
	String json = " { ";
	json += "\"name\" : \"" + asimName + "\", ";
	json += "\"simulation\" : \""+simId+ "\", ";
	json += "\"status\" : \"" + getStatus() + "\", ";
	json += "\"error\" : \"" + (getError() == null ? "" : getError()) + "\"";
	json += "}";

	return json;
    }

    // TODO: Synchronize this!!!
    public void pauseASIM() {
	running = false;
        paused = true;
    }

    // TODO: Synchronize this!!!
    public void resumeASIM() {
	running = true;
        paused = false;
    }

    // TODO: Synchronize this!!!
    public void stopASIM() {
        stopped = true;
        running = false;
        paused = false;
    }

    public boolean hasErrorOccurred() {
        return engine.hasErrorOccurred();
    }

    public CoreASIMError getError() {
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
                    if(!map.containsKey(t))
                        map.put(t, new UpdateMultiset());
                    map.get(t).add(update);
                }
            }
        }

        return map;
    }

    public void distributeUpdateSet(UpdateMultiset updates) {
        Map<String, UpdateMultiset> toSend = prepareUpdates(updates);
        Set<String> targets = toSend.keySet();

        String json = "";
        for(String target : targets) {
            try {
                json = mapper.writeValueAsString(toSend.get(target));
                MessageRequest req = new MessageRequest("update", simId, asimName, target, json);
                EngineManager.sendUpdate(simId, req);
            } catch (Exception e) {
                System.err.println("[ASIM "+asimName+"] Unable to transform UpdateSet into json.");
                System.err.println("[ASIM "+asimName+"] "+e);
                e.printStackTrace();
            }
        }
    }

    public synchronized void newASIM(String name) {
        asimsToAdd.add(name);
    }

    public synchronized void delASIM(String name) {
        asimsToDel.add(name);
    }

    public synchronized void injectASIMs() {
        engine.addASIMs(asimsToAdd);

        HashSet<String> copy = new HashSet<String>();
        copy.addAll(asimsToDel);
        engine.deleteASIMs(asimsToDel);

        asimsToDel.clear();
        asimsToAdd.clear();
    }

    // TODO: NEEDS TO BE SYNCHRONIZED!!!
    public boolean injectUpdates() {
        try {
            engine.updateState(new HashSet<Update>(updateMap.values()));
            updateMap.clear();
        } 
        catch(InconsistentUpdateSetException incUpdate) {
            System.err.println("Refuse update as it is inconsistent.");
        } 
        catch(InvalidLocationException invalidLoc) {
            System.err.println("Refuse update as a location is invalid.");
        }

        return true;
    }

    public boolean receiveUpdate(MessageRequest req) {
        String strUpdates = req.body;
        UpdateMultiset updates = null;
        try {
            updates = mapper.readValue(strUpdates, UpdateMultiset.class);
        } catch (IOException ioe) {
            System.err.println("Unable to transform JSON '"+strUpdates+"' into UpdateMultiset.");
            System.err.println(ioe);
        }
        Iterator<Update> it = updates.iterator();

        // introduce a scope
        for(Update u : updates) {
            // u.loc.args.add(0, new StringElement(req.fromAgent));
            List<Element> newArgs = new ArrayList<>();
            newArgs.add(new StringElement(req.fromAgent));
            newArgs.addAll(u.loc.args);
            Location newLoc = new Location(u.loc.name, newArgs);
            Update newUpdate = new Update(newLoc, u.value, u.action, (Element)null, null);
            Update oldUpdate = updateMap.get(newLoc);
            /* if(oldUpdate != null) {
                System.out.println("OVERWRITING OLD UPDATE IN LOCATION "+newLoc);
                System.out.println("New value of "+newLoc+": "+newUpdate.value);
                System.out.println("Old value of "+newLoc+": "+oldUpdate.value);
		}
	    */
            updateMap.put(newLoc, newUpdate);
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
        running = true;
	
        // EngineManager.updateLocationRegistrations(asimName);

        do {

            if(engine == null || engine.getEngineMode().equals(EngineMode.emTerminated)) {
                engine = null;
                break;
            }

            try {
                Thread.sleep(delay);
                if(paused) {
                    Thread.sleep(500);
                    continue;
                }
            } catch (InterruptedException ie) {
                // TODO REPORT STH HERE
            }

	    if (currentStep == 1)
		lastUpdateSet = new UpdateMultiset();

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
                    System.out.println("["+asimName+"]: ASIM "+name+" created.");
                }
                engine.reportNewAgents(agents);
            }
            engine.waitWhileBusy();
	    
	    if (engine.getEngineMode() == EngineMode.emError) {
                System.err.println("[ASIM Execution ERROR]: "+asimName+": "+engine.getError());
            }

            handleOutgoingMessages();
            deleteASIMs();

            lastUpdateSet = new UpdateMultiset(engine.getUpdateSet(0));
            distributeUpdateSet(lastUpdateSet);

	    currentStep++;

	    // System.err.println("step: " + currentStep);
            
	} while(true && !stopped);

        engine.terminate();
        engine.waitWhileBusy();
    }

    public void deleteASIMs() {
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
        CoreASIMEngine tempEngine = CoreASIMEngineFactory.createEngine();

        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger) {
        	ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)root;
            rootLogger.setLevel(ch.qos.logback.classic.Level.ERROR);
        } else {
        	logger.warn("Could not turn of logging.");
        }

        tempEngine.setProperty(EngineProperties.MAX_PROCESSORS, "1");
        tempEngine.setProperty(EngineProperties.AGENT_EXECUTION_THREAD_BATCH_SIZE, "1");

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
                EngineManager.registerLocations(asimName+"@"+asimName, simId, requiredLocs);
                return true;
            }
        } else {
            parseError = getError();
            inError = true;

            return false;
        }
    }

    public boolean isInError() {
        return inError;
    }

    public void destroy() {
        if(engine != null) {
            engine.terminate();
            engine.waitWhileBusy();
        }
    }
}
