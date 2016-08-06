/*
 * EngineManager.java v1.0
 *
 * This file contains source code developed by the European
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 *
 */


package org.coreasim.biomics;

import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import javax.ws.rs.ProcessingException;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.AgentCreationElement;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EngineManager {

    private static Wrapper wrapper = null;

    private static int asimCounter = 0;

    private static final HashMap<String, HashMap<String, CoreASMContainer>> asims = new HashMap<>();

    private static final HashMap<String, ArrayList<UpdateRegistrationRequest>> registrationRequests = new HashMap<>();

    public static String getASIMs() {
        String response = "{";
        boolean simcomma = false;

        Set<String> simulations = asims.keySet();
        for(String simulation : simulations) {
            if(!simcomma)
                simcomma = true;
            else
                response += ", ";

            response += simulation + " : [";
            Set<String> asimNames = asims.get(simulation).keySet();
            boolean comma = false;
            for(String name : asimNames) {
                if(!comma)
                    comma = true;
                else
                    response += ", ";

                response += name;
            }

            response += "]";
        }

        response += "}";
            
        return response;
    }

    public static String getASIMs(String simulation) {
        String response = "{ \""+simulation+"\" : [";

        if(!asims.containsKey(simulation))
            return null;
        else {
            Set<String> asimNames = asims.get(simulation).keySet();
            boolean comma = false;
            for(String name : asimNames) {
                if(!comma)
                    comma = true;
                else 
                    response += ", ";

                response += "\"" + name + "\"";
            }
        }

        response += "]}";

        return response;
    }

    public static String getASIM(String simulation, String asimName) {
        String response = "{ \""+asimName+"\" : ";
	
        if(!asims.containsKey(simulation))
            return null;
        else {
            if(!asims.get(simulation).containsKey(asimName))
		return null;
	    else {
		CoreASMContainer casm = asims.get(simulation).get(asimName);

		response += casm.toJSON();
            }
        }

        response += " }";

        return response;
    }

    public static CoreASIMError createASIM(ASIMCreationRequest req) {
        asimCounter++;

        if(req.simulation == null || req.simulation.equals(""))
            return new CoreASIMError("ASIM specification does not define simulation.");

        if(req.init == null || req.init.equals(""))
            return new CoreASIMError("ASIM specification does not define init rule.");

        if(req.name == null || req.name.equals(""))
            return new CoreASIMError("ASIM specification does not define a name.");
 
        if(req.signature == null || req.signature.equals(""))
            return new CoreASIMError("ASIM specification requires a signature.");

        if(req.program == null || req.program.equals(""))
            return new CoreASIMError("ASIM specification does not define a program.");

        if(req.policy == null || req.policy.equals(""))
            return new CoreASIMError("ASIM specification does not define a policy.");

        String program = "CoreASIM "+req.name+"\n\n";

        program += "use Standard\n\n";

        program += req.signature+"\n\n";
        
        String newRule = "RULE"+UUID.randomUUID().toString().replace("-","");

        program += "rule " + newRule + " = {\n"+req.init+"\nprogram(self) := "+req.program+"\n}\n\n";
        program += "init " + newRule + "\n\n";

        program += "scheduling "+req.policy+"\n\n";

        // System.out.println("Program to execute:\n"+program);

        int delay = 200;
        if(wrapper.config.schedulingMode)
            delay = 200;

        CoreASMContainer casm = new CoreASMContainer(req.simulation, req.name, program, delay);

        CoreASIMError error = null;
        synchronized(casm) {
            if(casm.hasErrorOccurred()) {
                error = casm.getError();
                return error;
            }
        }

        synchronized(asims) {
            if(asims.containsKey(req.simulation))
                asims.get(req.simulation).put(req.name, casm);
            else {
                asims.put(req.simulation, new HashMap<String, CoreASMContainer>());
                asims.get(req.simulation).put(req.name, casm);
            }
        }

        // always required? Makes only sense for schedulers
        updateLocationRegistrations();

        if(req.start)
            casm.start();

        return null;
    }
    
    public static int controlASIM(String simulation, String name, String cmd) {
        Map<String, CoreASMContainer> simASIMs = null;
        synchronized(asims) {
            if(asims.containsKey(simulation)) {
                simASIMs = asims.get(simulation);
            }
        }

	if(simASIMs != null) {
            if(simASIMs.containsKey(name)) {
                CoreASMContainer casm = simASIMs.get(name);
                synchronized(casm) {                    
                    switch(cmd) {
                    case "start":
                        if(!casm.isRunning())
                            casm.start();
                        break;
                    case "pause":
                        casm.pauseASIM();
                        break;
                    case "resume":
                        casm.resumeASIM();
                        break;
                    case "stop":
                        if(casm.isRunning()) {
                            casm.stopASIM();
                            try {
                                casm.join();
                            } 
                            catch(Exception e) {
                                // nothing
                            }
                            
                            CoreASMContainer newCASM = new CoreASMContainer(casm.simId, casm.asimName, casm.asimProgram, casm.delay);
                            casm.destroy();
                            casm = null;

                            asims.get(simulation).put(name, newCASM);
                            updateLocationRegistrations();
                        }

                        break;
                    default:
			return 400;
                    }
                    return 200;
                }
            } else {
                return 404;
            }
        } else
            return 404;
    }

    public static String getASIMState(String simulation, String name) {
	Map<String, CoreASMContainer> simASIMs = null;
        synchronized(asims) {
            if(asims.containsKey(simulation)) {
                simASIMs = asims.get(simulation);
            }
        }
	
        if(simASIMs != null) {
            if(simASIMs.containsKey(name)) {
                CoreASMContainer casm = simASIMs.get(name);
		return casm.getStatus();
	    }
	}

	return "unknown";
    }

    public static boolean receiveMsg(String simId, MessageRequest req) {
        /* System.out.println(wrapper.config.port+"Engine Manager receives message");
        System.out.println(wrapper.config.port+"Simulation: "+req.simulation);
        System.out.println(wrapper.config.port+"Receiver: "+req.toAgent);
        System.out.println(wrapper.config.port+"Sender: "+req.fromAgent);
        System.out.println(wrapper.config.port+"Body: "+req.body);   
        System.out.println(wrapper.config.port+"Type: "+req.type); 

        String s = Thread.currentThread().getName();
        System.out.println("[Thread "+s+"]: Msg from Sender: "+req.fromAgent+"; Body: "+req.body);*/
            
        // this is an ASIM message and must be 
        // forwarded to the correct ASIM
        if(req.type.equals("msg")) {
            String asim = "";
            
            CoreASMContainer trg = null;
            synchronized(asims) {
                // this message is sent to a simulation which
                // is not hosted by this brapper
                if(!asims.containsKey(simId))
                    return false;

                Map<String, CoreASMContainer> simAsims = asims.get(simId);
		
		// check whether this ASIM is managed here
                String[] names = req.toAgent.split("@");
                if(names.length != 2) {
                    System.err.println("EngineManager detects wrong address format in '"+req.toAgent+"'");
                    return false;
                } else 
                    asim = names[1];

		// TODO: Also check whether the agent (names[0]) is actually running inside the ASIM
                
                if(!simAsims.containsKey(asim)) {
                    System.err.println("EngineManager does not know this ASIM. Wrong delivery!");
                    return false;
                }
                
                req.toAgent = asim;
            
                trg = simAsims.get(asim);
            }

            boolean result = false;
            synchronized(trg) {
                result = trg.receiveMsg(req);
            }

            return result;
        }

        System.err.println("EngineManager receives unknown message type. Ignored!");

        return false;
    }

    public static boolean register4Updates(String simId, UpdateRegistrationRequest req) {
        // no target asim given
        if(req.target == null)
            return false;

        if(registrationRequests.get(simId) == null)
            registrationRequests.put(simId, new ArrayList<UpdateRegistrationRequest>());
        registrationRequests.get(simId).add(req);

        // this message is sent to a simulation which
        // is not hosted by this brapper - store for later
        if(!asims.containsKey(simId)) {
            return true;
        }
   
        Map<String, CoreASMContainer> simAsims = null;
        synchronized(asims) {
            simAsims = asims.get(simId);
        }
        
        if(simAsims == null)
            return false;
        
        List<UpdateLocation> registrations = req.registrations;
        for(UpdateLocation reg : registrations) {
            if(reg.location == null)
                continue;

            // only register with one ASIM
            if(reg.asim != null) {
                String asim = null;
                String asimAddress[] = reg.asim.split("@");
                if(asimAddress.length != 2) {
                    System.err.println("Invalid ASIM address in update registration: '"+reg.asim+"'");
                    continue;
                } else
                    asim = asimAddress[1];

                if(!simAsims.containsKey(asim)) {
                    System.err.println("EngineManager does not know this ASIM. Ignore update registration.");
                    continue;
                }
                CoreASMContainer trg = simAsims.get(asim);
                synchronized(trg) {
                    if(trg != null)
                        trg.register4Update(req.target, reg.location);
                }
            } 
            // register with all ASIMs at this brapper in this simulation
            else {
                Set<String> allAsims = simAsims.keySet();
                for(String asimName : allAsims) {
                    CoreASMContainer trg = simAsims.get(asimName);
                    synchronized(trg) {
                        trg.register4Update(req.target, reg.location);
                    }
                }
            }
        }

        return true;
    }

    public static boolean newASIM(String simId, String name) {
        if(wrapper.config.schedulingMode) {
            // this message is sent to a simulation which
            // is not hosted by this brapper
            if(!asims.containsKey(simId))
                return false;

            synchronized(asims) {
                Map<String, CoreASMContainer> simAsims = asims.get(simId);
                Set<String> allASIMs = simAsims.keySet();
                for(String trg : allASIMs) {
                    System.out.println("[Scheduler ASIM '"+trg+"'] New ASIM '"+name+"' registered.");
                    simAsims.get(trg).newASIM(name);
                }
            }
        } else
            return false;
        return true;
    }

    public static boolean delASIM(String simId, String name) {
        if(wrapper.config.schedulingMode) {
            // this message is sent to a simulation which
            // is not hosted by this brapper
            if(!asims.containsKey(simId))
                return false;

            synchronized(asims) {
                Map<String, CoreASMContainer> simAsims = asims.get(simId);
                Set<String> allASIMs = simAsims.keySet();
                for(String trg : allASIMs) {
                    System.out.println("[Scheduler ASIM '"+trg+"'] ASIM '"+name+"' deregistered.");
                    simAsims.get(trg).delASIM(name);
                }
            }
        } else
            return false;

        return true;
    }

    public static boolean receiveUpdate(String simId, MessageRequest req) {
        String agent = "";

        if(wrapper.config.accUpdatesMode) {
            if(req.type.equals("update")) {

                CoreASMContainer trg = null;
                
                synchronized(asims) {
                    // this message is sent to a simulation which
                    // is not hosted by this brapper
                    if(!asims.containsKey(simId))
                        return false;
                    
                    Map<String, CoreASMContainer> simAsims = asims.get(simId);
                    
                    String asim = null;
                    String asimAddress[] = req.toAgent.split("@");
                    if(asimAddress.length != 2) {
                        System.err.println("Invalid target address '"+req.toAgent+"'");
                        return false;
                    } else
                        asim = asimAddress[1];
                    
                    if(!simAsims.containsKey(asim)) {
                        // System.out.println("EngineManager does not know this ASIM");
                        return false;
                    }
                    
                    req.toAgent = asim;

                    trg = simAsims.get(asim);
                }
                
                boolean result = false;
                synchronized(trg) {
                    if(trg != null)
                        result = trg.receiveUpdate(req);
                }

                return result;
            } else {
                System.err.println("WARNING: Ignore message as it is not an update!");
                return false;
            }
        } else {
            System.out.println("WARNING: Update received although brapper is not in update accumulation mode. Ignored!");
            return false;
        }
    }

    public static void sendMsg(MessageRequest req) {
        if(wrapper == null) {
            System.err.println("FATAL: Unable to send message. No Wrapper!");
            System.exit(1);
        }

        String sim = req.simulation;
        boolean deliver = false;

        synchronized(asims) {
            // we host the simulation
            if(asims.containsKey(sim)) {
                String asimAddress[] = req.toAgent.split("@");
                if(asimAddress.length != 2) {
                    System.err.println("Invalid target address '"+req.toAgent+"'");
                    return;
                }
                String trg = asimAddress[1];
                
                Map<String, CoreASMContainer> hostedASIMs = asims.get(sim);
                // ASIM is also hosted here and can be delivered locally
                
                if(hostedASIMs.containsKey(trg)) {
                    // ASIM is hosted here, deliver directly
                    deliver = true;
                }
                    
            }
        }
         
        if(deliver) {
            receiveMsg(req.simulation, req);
            return;
        }
            
        // we cannot deliver if there is no manager
        if(wrapper.config.managerHost == null)
            return;
        
        String json = MessageRequest.getJSON(req);
        
        try {
            Response response = ClientBuilder.newBuilder()
                .build()
                .target(wrapper.commUrl)
                .path("message/"+req.simulation)
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .put(Entity.json(json));

            response.close();
        } 
        catch (ProcessingException pe) {
            System.err.println("Problem processing: "+pe);
            System.err.println("Cause: "+pe.getCause());
        } catch (Exception exception) {
            System.err.println(exception);
        }
    }

    public static void updateLocationRegistrations() {
        for(String sim : registrationRequests.keySet()) {
            ArrayList<UpdateRegistrationRequest> urs = registrationRequests.get(sim);

            for(UpdateRegistrationRequest ur : urs) {

                List<UpdateLocation> registrations = ur.registrations;
                for(UpdateLocation reg : registrations) {
                    if(reg.location == null)
                        continue;

                    HashMap<String, CoreASMContainer> simAsims = asims.get(sim);
                    Set<String> allAsims = simAsims.keySet();
                    for(String asimName : allAsims) {
                        CoreASMContainer trg = simAsims.get(asimName);
                        synchronized(trg) {
                            trg.register4Update(ur.target, reg.location);
                        }
                    }
                }
            }
	}
    }

    // TODO - request does not report ASIM in local asims map of this brapper
    // TODO - kill all asims!!!
    public static String requestASIMCreation(AgentCreationElement req, String simId) {
        if(wrapper == null) {
            System.err.println("FATAL: EngineManager cannot access Brapper!");
            System.exit(1);
        }

        // this engine is not managed, creation of ASIM must take place locally
        if(wrapper.config.managerHost == null) {
            String newName = "ASIM" + UUID.randomUUID().toString().replace("-", "");

            ASIMCreationRequest localReq = new ASIMCreationRequest(req, simId);
            localReq.start = true;
            if(localReq.name == null || localReq.name.equals(""))
                localReq.name = newName;

            CoreASIMError e = createASIM(localReq);
            
            if(e != null) {
                System.err.println(e);
                return null;
            } else {
                synchronized(asims) {
                    Map<String, CoreASMContainer> hostedASIMs = asims.get(simId);
                    if(hostedASIMs != null) {
                        CoreASMContainer casm = hostedASIMs.get(newName);
                    
                        // and start the ASIM directly
                        if(casm != null)
                            casm.start();
                        else
                            return null;
                    } else {
                        
                        return null;
                    }
                }

                return newName;
            }
        }

        String json = "{ \"simulation\" : \"" + simId + "\", " + req.toJSON().substring(1);
        Response response = null;
        try {
            response = ClientBuilder.newBuilder()
                .build()
                .target(wrapper.commUrl)
                .path("asims/")
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .put(Entity.json(json));
        } 
        catch (ProcessingException pe) {
            System.err.println("Problem processing: "+pe);
            System.err.println("Cause: "+pe.getCause());
        } catch (Exception exception) {
            System.err.println(exception);
        }

        ObjectMapper mapper = new ObjectMapper();
        
        if(response.getStatus() == 201) {
            String strResponse = response.readEntity(String.class);
            ASIMCreationResponse res = null;
            try {
                res = mapper.readValue(strResponse, ASIMCreationResponse.class);
            } catch (IOException ioe) {
                System.err.println("Invalid Response from Manager: '"+strResponse+"'");
                System.err.println(ioe);
            }
            
            response.close();

            return res.asim.name;
        }

        // TODO WHAT IF RESPONSE CODE IS NOT 201

        return null;
    }

    public static void sendUpdate(String simId, MessageRequest req) {
        if(wrapper == null) {
            System.err.println("ERROR: Running without a wrapper!");
            System.exit(1);
        }
        
        if(wrapper.getConfig().getManager() == null || wrapper.commUrl == null) {
            System.err.println("No manager available. Don't send updates");
            return;
        }

        String json = MessageRequest.getJSON(req);

        try {
            Response response = ClientBuilder.newBuilder()
                .build()
                .target(wrapper.commUrl)
                .path("updates/"+simId)
                .request(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .put(Entity.json(json));

            response.close();
        } 
        catch (ProcessingException pe) {
            System.err.println("ERROR: Problem processing Update: "+pe);
            System.err.println("ERROR: Cause: "+pe.getCause());
        } catch (Exception exception) {
            System.err.println(exception);
        }
    }
    
    public static void stopAll() {
        
    }

    public static void reset(Wrapper wrapper) {
        EngineManager.wrapper = wrapper;

        if(wrapper.config.managerHost != null)
            registerWithManager();

        synchronized(asims) {
            asims.clear();
        }
    }
    
    // regitser locations of scheduler with the simulation 
    public static boolean registerLocations(String asim, String simulation, Set<String> locs) {
        boolean success = false;

        if(wrapper.config.schedulingMode) {

            String registrations = "{ \"target\" : \""+asim+"\",";
            registrations += " \"registrations\" : [ ";
            
            boolean comma = false;
            for(String l : locs ) {
                if(comma)
                    registrations += ",";
                registrations += " { \"location\" : \"" + l + "\" } ";
                comma = true;
            }

            registrations += "] }";

            try {
                Response response = ClientBuilder.newBuilder()
                    .build()
                    .target(wrapper.commUrl)
                    .path("updates/"+simulation+"/register")
                    .request(MediaType.APPLICATION_JSON)
                    .accept("*/*")
                    .put(Entity.json(registrations));
                int status = response.getStatus(); 
                if(status == 201) {
                    success = true;
                } else {
                    success = false;
                    System.err.println("Unable to register locations for updates.");
                    System.err.println("Response: "+response.readEntity(String.class));
                }
                
                response.close();
            } 
            catch (ProcessingException pe) {
                System.err.println("Error: Problem registering brapper: "+pe);
                System.err.println("Cause: "+pe.getCause());
            } catch (Exception exception) {
                System.err.println("Error: Problem registering brapper: "+exception);
            }
        }

        return success;
    }

    public static boolean requestASIMDeletion(String simId, Set<String> asims) {
        boolean success = true;

        for(String name : asims) {
            try {
                Response response = ClientBuilder.newBuilder()
                    .build()
                    .target(wrapper.commUrl)
                    .path("asims/"+simId+"/"+name)
                    .request(MediaType.APPLICATION_JSON)
                    .accept("*/*")
                    .delete();
                int status = response.getStatus(); 
                if(status == 200) {
                    // System.out.println(response.readEntity(String.class));
                } else {
                    System.err.println("Unable to delete ASIM '"+name+"'.");
                    success = false;
                }
                
                response.close();
            } 
            catch (ProcessingException pe) {
                System.err.println("Error: Problem registering brapper: "+pe);
                System.err.println("Cause: "+pe.getCause());
            } catch (Exception exception) {
                System.err.println("Error: Problem registering brapper: "+exception);
            }
        }

        return success;
    }

    public static boolean destroyASIM(String simId, String name) {
        CoreASMContainer casm = null;

        synchronized(asims) {
            if(!asims.containsKey(simId)) {
                return false;
	    }
	    
            casm = asims.get(simId).get(name);
            asims.get(simId).remove(name);
        }

        if(casm != null) {
            synchronized(casm) {
                casm.stopASIM();
                try {
                    casm.join();
                } 
                catch(Exception e) {
                    // nothing
                }
                casm = null;
                asims.get(simId).remove(name);
            }
	    return true;
        } else
	    return false;
    }

    private static void registerWithManager() {
        if(wrapper.config.schedulingMode)
            return;

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
                // System.out.println(response.readEntity(String.class));
            } else {
                System.err.println("Unable to register brapper.");
            }

            response.close();
        } 
        catch (ProcessingException pe) {
            System.err.println("Error: Problem registering brapper: "+pe);
            System.err.println("Cause: "+pe.getCause());
        } catch (Exception exception) {
            System.err.println("Error: Problem registering brapper: "+exception);
        }
    }
}
