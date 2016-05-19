package org.coreasm.biomics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import org.coreasm.engine.CoreASMError;

public class ASIMCreationResponse {
    @JsonProperty("name") public String name;
    @JsonProperty("simulation") public String simulation;
    @JsonProperty("success") public boolean success;
    @JsonProperty("error") public String error;

    @JsonCreator
    public ASIMCreationResponse(@JsonProperty("name") String n, @JsonProperty("simulation") String sim, @JsonProperty("success") boolean s, @JsonProperty("error") String e) {
        name = n;
        simulation = sim;
        success = s;
        error = e;
    }
}
