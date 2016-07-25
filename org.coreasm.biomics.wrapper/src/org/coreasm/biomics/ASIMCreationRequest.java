/*
 * ASIMCreationRequest.java v1.0
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
    public boolean start;

    @JsonCreator
    public ASIMCreationRequest(@JsonProperty("name") String n, 
                               @JsonProperty("simulation") String sim, 
                               @JsonProperty("signature") String sig,
                               @JsonProperty("init") String in,
                               @JsonProperty("program") String prog,
                               @JsonProperty("policy") String pol,
                               @JsonProperty("start") boolean s) {
        name = n;
        simulation = sim;
        signature = sig;
        init = in;
        program = prog;
        policy = pol;
        start = s;
    }

    public ASIMCreationRequest(AgentCreationElement e, String simId) {
        simulation = simId;
        name = e.getName().toString();

        signature = e.getSignature() + "\\n";
        signature = e.getInitRule().getDeclarationNode().unparseTree() + "\\n\\n";
        signature += e.getInitRule().getDeclarationNode().unparseTree() + "\\n\\n";
        signature += e.getProgram().getDeclarationNode().unparseTree() + "\\n\\n";
        signature += e.getPolicy().getDeclarationNode().unparseTree() + "\\n\\n";

        init = e.getInitRule().getName();
        program = e.getProgram().getName();
        policy = e.getPolicy().getName();
        start = true;
    }
}
