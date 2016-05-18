package org.coreasm.biomics;

import java.util.Map;
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

import org.coreasm.engine.CoreASMError;

public class EngineManager {

    private static Wrapper wrapper = null;

    private static final HashMap<String, HashMap<String, CoreASMContainer>> asims = new HashMap<>();

    public static CoreASMError createASIM(ASIMCreationRequest req) {
        System.out.println("Create new ASIM");
        System.out.println("ASIM Simulation: "+req.simulation);
        System.out.println("ASIM Name: "+req.name);
        System.out.println("ASIM Signature: "+req.signature);
        System.out.println("ASIM Init: "+req.init);
        System.out.println("ASIM Program: "+req.program);
        System.out.println("ASIM Policy: "+req.policy);

        if(req.simulation == null || req.simulation.equals(""))
            return new CoreASMError("ASIM specification does not define simulation.");

        if(req.init == null || req.init.equals(""))
            return new CoreASMError("ASIM specification does not define init rule.");

        if(req.name == null || req.name.equals(""))
            return new CoreASMError("ASIM specification does not define a name.");

        if(req.program == null || req.program.equals(""))
            return new CoreASMError("ASIM specification does not define a program.");

        if(req.policy == null || req.policy.equals(""))
            return new CoreASMError("ASIM specification does not define a policy.");

        String program = "CoreASM "+req.name+"\n\n";
        program += "use Standard\n\n";

        // the program signature may be empty
        if(req.signature != null && !req.signature.equals(""))
            program += req.signature+"\n\n";

        program += "init Start\n\n";
        program += "policy p = "+req.policy+"\n\n";
        program += "rule Start = {\n"+req.init+"\n\tprogram(self) := Main\n}\n\n";
        program += "rule Main = {\n"+req.program+"\n}\n\n";
        program += "scheduling p\n";

        System.out.println("Program to execute: "+program);

        CoreASMContainer casm = new CoreASMContainer(req.simulation, req.name, program);

        if(asims.containsKey(req.simulation))
            asims.get(req.simulation).put(req.name, casm);
        else {
            asims.put(req.simulation, new HashMap<String, CoreASMContainer>());
            asims.get(req.simulation).put(req.name, casm);
        }

        if(casm.hasErrorOccurred())
            return casm.getError();
        else
            return null;
    }
    
    public static boolean controlASIM(String simulation, String name, String cmd) {
        if(asims.containsKey(simulation)) {
            Map<String, CoreASMContainer> simASIMs = asims.get(simulation);
            if(simASIMs.containsKey(name)) {
                CoreASMContainer casm = simASIMs.get(name);
                
                switch(cmd) {
                case "start":
                    casm.start();
                    break;
                case "pause":
                    casm.pauseASIM();
                    break;
                case "resume":
                    casm.resumeASIM();
                    break;
                case "stop":
                default:
                }

                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    public static boolean receiveMsg(MessageRequest req) {
        System.out.println("Engine Manager receives message");
        System.out.println("Simulation: "+req.simulation);
        System.out.println("Receiver: "+req.toAgent);
        System.out.println("Sender: "+req.fromAgent);
        System.out.println("Body: "+req.body);   
        System.out.println("Type: "+req.type);   

        // this is an ASIM message and must be 
        // forwarded to the correct ASIM
        if(req.type.equals("msg")) {
            // System.out.println("try to forward msg to CoreASM");

            String asim = "";

            if(wrapper.config.managerHost != null) {
                // this message is sent to a simulation which
                // is not hosted by this brapper
                if(!asims.containsKey(req.simulation))
                    return false;

                Map<String, CoreASMContainer> simAsims = asims.get(req.simulation);

                // check whether this ASIM is managed here
                String[] names = req.toAgent.split("@");
                if(names.length != 2) {
                    System.out.println("EngineManager detects wrong address format");
                    return false;
                } else 
                    asim = names[1];
                
                if(simAsims.containsKey(asim)) {
                    System.out.println("EngineManager knows this ASIM");
                } else {
                    System.out.println("EngineManager does not know this ASIM");
                    return false;
                }
                req.toAgent = asim;

                CoreASMContainer trg = simAsims.get(asim);

                return trg.receiveMsg(req);
            } else {
                // TODO - forward the message to all possible agents that match specified target
                /* for(CoreASMContainer trg : asims.values()) {
                   trg.receiveMsg(req);
                   }
                */ 
            }

            return true;
        }

        System.out.println("EngineManager receives unknown message type. Ignore!");

        return false;
    }

    public static boolean receiveUpdate(MessageRequest req) {
        System.out.println("Engine Manager receives update");

        String agent = "";

        /* if(req.type.equals("update")) {
            if(wrapper.config.accUpdatesMode) {
                String[] names = req.fromAgent.split(":");
                if(names.length > 1)
                    agent = names[1];
                else
                    agent = names[0];

                if(asims.containsKey(agent)) {
                    System.out.println("EngineManager knows this ASIM");
                } else {
                    System.out.println("EngineManager does not know this ASIM");
                    return false;
                }
                req.fromAgent = agent;

                CoreASMContainer trg = asims.get(agent);
                trg.receiveUpdate(req);
            } else {
                System.err.println("WARNING: Update received although wrapper is not in update accumulation mode. Ignored!");
                return false;
            }
            }*/

        return true;
    }

    public static void sendMsg(MessageRequest req) {
        if(wrapper == null || wrapper.commUrl == null) {
            System.err.println("FATAL: Unable to send message. Target unknown!");
            System.exit(1);
        }

        String json = MessageRequest.getJSON(req);

        try {
            Response response = ClientBuilder.newBuilder()
                // .register(JacksonFeature.class) // would be the better way but did not get it working
                .build()
                .target(wrapper.commUrl)
                .path("message/"+req.simulation)
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .put(Entity.json(json));
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

        String json = MessageRequest.getJSON(req);

        try {
            Response response = ClientBuilder.newBuilder()
                .build()
                .target(wrapper.commUrl)
                .path("update")
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .put(Entity.json(json));
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

        if(wrapper.config.managerHost != null) {
            registerWithManager();
        }

        asims.clear();
    }

    private static void registerWithManager() {
        System.out.print("Try to register brapper with manager ... ");

        String registration = "{ \"host\" : \""+ 
            wrapper.config.getHost() + "\", \"port\" : \"" + 
            wrapper.config.getPort() + "\" }";

        try {
            Response response = ClientBuilder.newBuilder()
                .build()
                .target(wrapper.commUrl)
                .path("brappers")
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .put(Entity.json(registration));
            int status = response.getStatus(); 
            if(status == 200) {
                System.out.println("Success.");
                System.out.println(response.readEntity(String.class));
            } else {
                System.out.println("Fail.");
            }
        } 
        catch (ProcessingException pe) {
            System.out.println("Fail.");
            System.out.println("\tError: Problem registering brapper: "+pe);
            System.out.println("\tCause: "+pe.getCause());
        } catch (Exception exception) {
            System.out.println("Fail.");
            System.out.println("\tError: Problem registering brapper: "+exception);
        }
    }
}
