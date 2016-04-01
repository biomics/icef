package org.coreasm.biomics;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@Path("message")
public class MessageResource {

    @PUT
    @Consumes("application/json")
    @Produces("text/plain")
    public String receiveMsg(String message) {
        System.out.println("Wrapper receives Message");
        System.out.println("JSON: "+message);

        MessageRequest req = null;
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            req = mapper.readValue(message, MessageRequest.class);
        } catch (IOException ioe) {
            System.err.println("Invalid creation request: '"+message+"'");
            System.err.println(ioe);
        }

        boolean result = false;
        if(req != null) {
            result = EngineManager.receiveMsg(req);
        }

        if(result)
            return "Success.\n";
        else 
            return "Fail!\n";
    }
}
