package org.coreasm.biomics;

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
            return Response.status(200).build();
        } else {
            return Response.status(403).build();
        }
    }

    @PUT
    @Path("/{simId}/asim/{name}")
    @Consumes("application/json")
    public Response newASIM(@PathParam("simId") String simId, @PathParam("name") String name) {
        boolean result = false;
        
        result = EngineManager.newASIM(simId, name);

        if(result) {
            return Response.status(201).build();
        } else {
            return Response.status(403).build();
        }
    }

    @DELETE
    @Path("/{simId}/asim/{name}")
    @Consumes("application/json")
    public Response removeASIM(@PathParam("simId") String simId, @PathParam("name") String name) {
        boolean result = false;

        System.out.println("UpdateResource.removeASIM");
        
        result = EngineManager.delASIM(simId, name);

        if(result) {
            return Response.status(201).build();
        } else {
            return Response.status(403).build();
        }
    }

    @PUT
    @Path("/{simId}/register")
    @Consumes("application/json")
    public Response register4Updates(@PathParam("simId") String simId, String update) {
        UpdateRegistrationRequest req = UpdateRegistrationRequest.getUpdateRegistrationRequest(update);

        System.out.println("register4Updates");

        boolean result = false;
        if(req != null && req.target != null) {
            result = EngineManager.register4Updates(simId, req);
        }

        if(result) {
            return Response.status(200).build();
        } else {
            return Response.status(403).build();
        }
    }
}
