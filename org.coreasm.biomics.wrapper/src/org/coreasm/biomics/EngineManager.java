package org.coreasm.biomics;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import javax.ws.rs.ProcessingException;

import org.glassfish.jersey.jackson.JacksonFeature;


public class EngineManager {

    private static Wrapper wrapper = null;

    private static final HashMap<String, CoreASMContainer> engines = 
        new HashMap<>();

    public static void createEngine(AgentCreationRequest req) {
        System.out.println("Create a new engine");
        System.out.println("AgentName: "+req.name);
        System.out.println("Program: "+req.program);

        CoreASMContainer casm = new CoreASMContainer(req.name, req.program);
        engines.put(req.name, casm);
    }

    public static boolean pauseEngine(String name) {
        if(engines.containsKey(name)) {
            CoreASMContainer casm = engines.get(name);
            try {
                casm.wait();
            } catch (InterruptedException e) {
                // TODO Acount for this.
                System.err.println("Unable to put engine to sleep");
            }
            
            return true;
        } else {
            return false;
        }
    }

    public static boolean resumeEngine(String name) {
        if(engines.containsKey(name)) {
            CoreASMContainer casm = engines.get(name);
            casm.notify();
            
            return true;
        } else {
            return false;
        }
    }

    public static boolean startEngine(String name) {
        System.out.println("Start an existing engine");
        System.out.println("AgentName: "+name);

        if(engines.containsKey(name)) {
            CoreASMContainer casm = engines.get(name);
            casm.start();
            
            return true;
        } else {
            return false;
        }
    }

    public static boolean receiveMsg(MessageRequest req) {
        System.out.println("Engine Manager receives message");
        System.out.println("Receiver: "+req.toAgent);
        System.out.println("Sender: "+req.fromAgent);
        System.out.println("Body: "+req.body);   
        System.out.println("Type: "+req.type);   

        // this is an agent message and must be 
        // forwarded to the correct agent
        if(req.type.equals("agent")) {
            // System.out.println("try to forward msg to CoreASM");

            String agent = "";

            if(wrapper.config.managerHost != null) {
                // check whether this agent is managed here
                String[] names = req.toAgent.split(":");
                if(names.length > 1)
                    agent = names[1];
                else
                    agent = names[0];
                
                if(engines.containsKey(agent)) {
                    System.out.println("EngineManager knows this agent");
                } else {
                    System.out.println("EngineManager does not know this agent");
                    return false;
                }
                req.toAgent = agent;

                CoreASMContainer trg = engines.get(agent);
                trg.receiveMsg(req);
            } else {
                // TODO - forward the message to all possible agents that match specified target
                for(CoreASMContainer trg : engines.values()) {
                    trg.receiveMsg(req);
                }
            }

            return true;
        } else {
            if(req.type.equals("update")) {
                String agent = "";

                
            }
        }

        System.out.println("EngineManager receives unknown message type. Ignore!");

        return false;
    }

    public static void sendMsg(MessageRequest req) {
        if(wrapper == null || wrapper.commUrl == null) {
            System.err.println("ERROR: Unable to send message. Target unknown!");
            System.exit(1);
        }

        String json = MessageRequest.getJSON(req);

        try {
            Response response = ClientBuilder.newBuilder()
                // .register(JacksonFeature.class) // would be the better way but did not get it working
                .build()
                .target(wrapper.commUrl)
                .path("message")
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .post(Entity.json(json));
            // System.out.println("response.getStatusCode: "+response);
        } 
        catch (ProcessingException pe) {
            System.out.println("Problem processing: "+pe);
            System.out.println("Cause: "+pe.getCause());
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public static void sendUpdate(MessageRequest req) {
        if(wrapper == null) {
            System.err.println("ERROR: Running without a wrapper!");
            System.exit(1);
        }
        
        if(wrapper.getConfig().getManager() == null || wrapper.commUrl == null) {
            // no manager, so don't send updates
            return;
        }

        System.out.println("SEND UPDATES");

        String json = MessageRequest.getJSON(req);

        try {
            Response response = ClientBuilder.newBuilder()
                .build()
                .target(wrapper.commUrl)
                .path("update")
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .post(Entity.json(json));
            System.out.println("response: "+response);
        } 
        catch (ProcessingException pe) {
            System.out.println("Problem processing: "+pe);
            System.out.println("Cause: "+pe.getCause());
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
    
    public static void stopAll() {
        
    }

    public static void reset(Wrapper wrapper) {
        EngineManager.wrapper = wrapper;

        engines.clear();
    }
}
