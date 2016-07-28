/*
 * ASIMResource.java v1.0
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

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.coreasim.engine.CoreASMError;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("asims")
public class ASIMResource {

    // TODO replace all string responses with real JSON mappings to Java Objects

    @GET
    @Produces("application/json")
    public Response getAllASIMs() {
        String response = EngineManager.getASIMs();
        return Response.status(200).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{simId}")
    @Produces("application/json")
    public Response getASIMStatus(@PathParam("simId") String simulation ) {
        String response = EngineManager.getASIMs(simulation);
        
        if(response != null)
            return Response.status(200).entity(response).type(MediaType.APPLICATION_JSON).build();
        else
            return Response.status(404).entity("{ 'error' : 'Brapper does not host any ASIM for this simulation.'}").type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/{simId}/{asimName}")
    @Produces("application/json")
    public Response getASIMStatus(@PathParam("simId") String simulation, @PathParam("asimName") String asimName) {
        String response = EngineManager.getASIM(simulation, asimName);
        
        if(response != null)
            return Response.status(200).entity(response).type(MediaType.APPLICATION_JSON).build();
        else
            return Response.status(404).entity("{ 'error' : 'Brapper does not host this ASIM in the specified simulation.'}").type(MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/{simId}/{asimName}/{command}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response controlASIM(@PathParam("simId") String simId, @PathParam("asimName") String asimName, @PathParam("command") String command) {
        ObjectMapper mapper = new ObjectMapper();

        int code = EngineManager.controlASIM(simId, asimName, command);

        String error = "";
	if(code != 200) {
	    if(code == 404)
		error = "Cannot execute command '"+command+"'. Unable to find specified simulation or ASIM";
	    else
		if(code == 400)
		    error = "Unable to control ASIM with command '" + command + "'. Unknown command!";
	    return Response.status(code).entity(" { \"error\" : \""+error+"\" } ").type(MediaType.APPLICATION_JSON).build();
	}

        ASIMControlResponse res = new ASIMControlResponse(asimName, simId, EngineManager.getASIMState(simId, asimName), error);

        String json = "{}";
        try {
            json  = mapper.writeValueAsString(res);
        }  catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

	return Response.status(code).entity(json).type(MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/{simId}/{asimName}")
    @Consumes("application/json")
    public Response destroyASIM(@PathParam("simId") String simId, @PathParam("asimName") String asimName) {
        if(EngineManager.destroyASIM(simId, asimName))
            return Response.status(200).entity("{ \"msg\" : \"ASIM successfully deleted.\" }").type(MediaType.APPLICATION_JSON).build();
        else
            return Response.status(404).entity("{ \"error\" : \"ASIM or Simulation not found.\" }").type(MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response createNewASIM(String createParameters) {
        ObjectMapper mapper = new ObjectMapper();
        
        ASIMCreationRequest req = null;
        try {
            req = mapper.readValue(createParameters, ASIMCreationRequest.class);
        } catch (IOException ioe) {
            System.err.println("Invalid creation request: '"+createParameters+"'");
            System.err.println(ioe);
	    return Response.status(400).entity("{ msg : \"Unable to create ASIM. JSON specification of ASIM is incorrect: "+ioe+"\" }").type(MediaType.APPLICATION_JSON).build();
        }

        CoreASMError error = EngineManager.createASIM(req);

        ASIMCreationResponse res = new ASIMCreationResponse();
	res.setMsg("ASIM successfully created");
	res.asim.setName(req.name);
	res.asim.setSimulation(req.simulation);
	res.asim.setStatus("unknown");
	res.asim.setSuccess(error == null ? true : false);
	res.asim.setError(error == null ? "" : error.toString());
	
        String json = "{}";

        try {
            json = mapper.writeValueAsString(res);
        }  catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

	System.err.println("RESPONSE TO SEND: "+json);

        if(error != null)
            return Response.status(400).entity(json).type(MediaType.APPLICATION_JSON).build();
        else
            return Response.status(201).entity(json).type(MediaType.APPLICATION_JSON).build();
    }
}
