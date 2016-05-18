package org.coreasm.biomics;

import javax.ws.rs.PUT;
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


@Path("update")
public class UpdateResource {

    @PUT
    @Consumes("application/json")
    public Response receiveMsg(String update) {
        MessageRequest req = MessageRequest.getMessage(update);

        boolean result = false;
        if(req != null) {
            result = EngineManager.receiveUpdate(req);
        }

        if(result) {
            return Response.status(200).build();
        } else {
            return Response.status(403).build();
        }
    }
}
