package org.coreasm.biomics;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("asims")
public class AgentResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/{agentname}")
    @Produces("text/plain")
    public String getAgentName(@PathParam("agentname") String agentName) {
        System.out.println("agentName: '"+agentName+"'");

        return "Got it!";
    }

    @GET
    @Path("/{agentname}/start")
    @Produces("text/plain")
    public String startAgent(@PathParam("agentname") String agentName) {
        System.out.println("Start an existing agent");

        EngineManager.startEngine(agentName);

        return "Started";
    }

    @GET
    @Path("/{agentname}/pause")
    @Produces("text/plain")
    public String pauseAgent(@PathParam("agentname") String agentName) {
        if(EngineManager.pauseEngine(agentName)) {
            return "Agent '"+agentName+"' was paused.";
        } else {
            return "Unable to pause agent '"+agentName+"'.";
        }
    }

    @GET
    @Path("/{agentname}/resume")
    @Produces("text/plain")
    public String resumeAgent(@PathParam("agentname") String agentName) {
        if(EngineManager.resumeEngine(agentName)) {
            return "Agent '"+agentName+"' was resumed.";
        } else {
            return "Unable to resume agent '"+agentName+"'.";
        }
    }

    @GET
    @Produces("text/plain")
    public String getAgentName() {
        System.out.println("All currently running agents");

        return "Got it!";
    }

    @PUT
    @Path("create")
    @Consumes("application/json")
    @Produces("text/plain")
    public String createNewAgent(String createParameters) {
        System.out.println("Create a new agent");
        System.out.println("JSON: "+createParameters);

        ObjectMapper mapper = new ObjectMapper();
        
        AgentCreationRequest req = null;
        try {
            req = mapper.readValue(createParameters, AgentCreationRequest.class);
        } catch (IOException ioe) {
            System.err.println("Invalid creation request: '"+createParameters+"'");
            System.err.println(ioe);
        }

        // TODO: Check the generated AgentCreationRequest and throw a WebApplicationException
        // to respond to the client

        if(req != null) {
            EngineManager.createEngine(req);
            return "Success";
        }

        return "Fail";
    }
}
