package org.coreasm.biomics;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.Version;


@Path("message")
public class MessageResource {

    @PUT
    @Consumes("application/json")
    @Produces("text/plain")
    public String receiveMsg(String message) {
        System.out.println("MessageResource: receiveMsg");
        System.out.println("Message: "+message);
        
        MessageRequest req = MessageRequest.getMessage(message);

        boolean result = false;
        if(req != null) {
            result = EngineManager.receiveMsg(req);
        }

        if(result) {
            return "Success.\n";
        } else {
            return "Fail.\n";
        }
    }
}
