package org.coreasm.biomics;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public class UpdateRegistrationRequest {
    public String target;
    public List<UpdateLocation> registrations;

    @JsonCreator
    public UpdateRegistrationRequest(@JsonProperty("registrations") List<UpdateLocation> l, @JsonProperty("target") String t) {
        this.registrations = l;
        target = t;
    }

    public static UpdateRegistrationRequest getUpdateRegistrationRequest(String json) {
        ObjectMapper mapper = new ObjectMapper();

        UpdateRegistrationRequest req = null;

        try {
            req = mapper.readValue(json, UpdateRegistrationRequest.class);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to create update request object from JSON. Format does not comply!");
            System.err.println("ERROR: "+e.getMessage());
        }

        return req;
    }

    public static String getJSON(UpdateRegistrationRequest msg) {
        ObjectMapper mapper = new ObjectMapper();

        String json = "";

        try {
            json = mapper.writeValueAsString(msg);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to transform update request to JSON. Format does not comply!");
            System.err.println("ERROR: "+e.getMessage());
        }

        return json;
    }
}
