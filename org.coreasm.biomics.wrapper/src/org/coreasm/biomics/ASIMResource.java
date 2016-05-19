package org.coreasm.biomics;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.coreasm.engine.CoreASMError;

@Path("asims")
public class ASIMResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/{asimName}")
    @Produces("text/plain")
    public String getASIMStatus(@PathParam("asimName") String asimName) {
        System.out.println("asimName: '"+asimName+"'");

        return "Got it!";
    }

    @PUT
    @Path("/{simId}/{asimName}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response controlASIM(@PathParam("simId") String simId, @PathParam("asimName") String asimName, String controlParameters) {
        ObjectMapper mapper = new ObjectMapper();
        ASIMControlRequest req = null;
        try {
            req = mapper.readValue(controlParameters, ASIMControlRequest.class);
        } catch (IOException ioe) {
            System.err.println("Invalid control request: '"+controlParameters+"'");
            System.err.println(ioe);
        }

        boolean success = EngineManager.controlASIM(simId, asimName, req.command);

        String error = null;
        if(!success) {
            error = "Unable to '" + req.command + "' ASIM.";
        }

        ASIMControlResponse res = new ASIMControlResponse(asimName, simId, error);

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

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces("text/plain")
    public String getAllASIMs() {
        System.out.println("All currently running ASIMs");

        return "Got it!";
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response createNewASIM(String createParameters) {
        System.out.println("Create new ASIM");
        System.out.println("JSON: "+createParameters);

        ObjectMapper mapper = new ObjectMapper();
        
        ASIMCreationRequest req = null;
        try {
            req = mapper.readValue(createParameters, ASIMCreationRequest.class);
        } catch (IOException ioe) {
            System.err.println("Invalid creation request: '"+createParameters+"'");
            System.err.println(ioe);
        }

        CoreASMError error = EngineManager.createASIM(req);

        ASIMCreationResponse res = new ASIMCreationResponse(req.name, req.simulation, error == null ? true : false, error == null ? "" : error.toString());

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

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}
