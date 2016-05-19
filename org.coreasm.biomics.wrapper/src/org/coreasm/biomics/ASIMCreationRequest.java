package org.coreasm.biomics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import org.coreasm.engine.absstorage.AgentCreationElement;

public class ASIMCreationRequest {
    public String name;
    public String simulation;
    public String signature; 
    public String init; 
    public String program;
    public String policy;

    @JsonCreator
    public ASIMCreationRequest(@JsonProperty("name") String n, 
                               @JsonProperty("simulation") String sim, 
                               @JsonProperty("signature") String sig,
                               @JsonProperty("init") String in,
                               @JsonProperty("program") String prog,
                               @JsonProperty("policy") String pol) {
        name = n;
        simulation = sim;
        signature = sig;
        init = in;
        program = prog;
        policy = pol;
    }

    public ASIMCreationRequest(AgentCreationElement e, String simId) {
        simulation = simId;
        name = e.getName().toString();
        signature = e.getSignature();
        init = e.getInitRule().getBody().unparseTree();
        program = e.getProgram().getBody().unparseTree();
        policy = e.getPolicy().getBody().unparseTree();
    }
}
