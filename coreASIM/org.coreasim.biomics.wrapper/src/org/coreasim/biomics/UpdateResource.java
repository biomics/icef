/*
 * UpdateResource.java v1.0
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

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.Version;

@Path("updates")
public class UpdateResource {

    @PUT
    @Path("/{simId}")
    @Consumes("application/json")
    public Response receiveUpdate(@PathParam("simId") String simId, String update) {
        MessageRequest req = MessageRequest.getMessage(update);

        boolean result = false;
        if(req != null) {
            result = EngineManager.receiveUpdate(simId, req);
        }

        if(result) {
            return Response.status(200).entity("{ \"msg\" : \"Update received and accumulated successfully.\" }").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(404).entity("{ \"msg\" : \"Unable to process update.\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }

    @PUT
    @Path("/{simId}/{name}")
    @Consumes("application/json")
    public Response newASIM(@PathParam("simId") String simId, @PathParam("name") String name) {
        boolean result = false;
        result = EngineManager.newASIM(simId, name);

        if(result) {
            return Response.status(201).entity("{ \"msg\" : \"New ASIM has been registered successfully.\" }").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(404).entity("{ \"msg\" : \"Unable to register new ASIM in this brapper.\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }

    @DELETE
    @Path("/{simId}/{name}")
    @Consumes("application/json")
    public Response removeASIM(@PathParam("simId") String simId, @PathParam("name") String name) {
        boolean result = false;

        result = EngineManager.delASIM(simId, name);

        if(result) {
            return Response.status(200).entity("{ \"msg\" : \"ASIM successfully deregistered from brapper and its ASIMs.\"}").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(404).entity("{ \"msg\" : \"Unable to deregister ASIM in this brapper.\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }

    @PUT
    @Path("/{simId}/register")
    @Consumes("application/json")
    public Response register4Updates(@PathParam("simId") String simId, String update) {
        UpdateRegistrationRequest req = UpdateRegistrationRequest.getUpdateRegistrationRequest(update);

        boolean result = false;
        if(req != null && req.target != null) {
            result = EngineManager.register4Updates(simId, req);
        }

        if(result) {
            return Response.status(201).entity("{ \"msg\" : \"Registration for specified locations create successfully.\"}").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(403).entity("{ \"msg\" : \"Registration for specified locations was not successful.\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }
}
