package org.coreasm.biomics;

import org.coreasm.biomics.serializers.MessageRequestDeserializer;
import org.coreasm.biomics.serializers.MessageRequestSerializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class MessageRequest {
    public static ObjectMapper mapper = null;

    public String simulation;
    public String fromAgent;
    public String toAgent;
    public String body;
    public String type;

    public MessageRequest(String _type, String _simulation, String _from, String _to, String _body) {
        type = _type;
        simulation = _simulation;
        fromAgent = _from;
        toAgent = _to;
        body = _body;
    }

    public MessageRequest(String _type, String _simulation, String _from, String _body) {
        type = _type;
        simulation = _simulation;
        fromAgent = _from;
        toAgent = null;
        body = _body;
    }

    public static void prepareMapper() {
        if(mapper == null) {
            mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule("MessageRequest Serializer", new Version(0,1,1,"FINAL"));
            module.addSerializer(MessageRequest.class, new MessageRequestSerializer());
            module.addDeserializer(MessageRequest.class, new MessageRequestDeserializer());
            mapper.registerModule(module);
        }
    }

    public static MessageRequest getMessage(String json) {
        prepareMapper();

        MessageRequest req = null;

        try {
            req = mapper.readValue(json, MessageRequest.class);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to create message object from JSON. Format does not comply!");
            System.err.println("ERROR: "+e.getMessage());
        }

        return req;
    }

    public static String getJSON(MessageRequest msg) {
        prepareMapper();

        String json = "";

        try {
            json = mapper.writeValueAsString(msg);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to transform msg to JSON. Format does not comply!");
            System.err.println("ERROR: "+e.getMessage());
        }

        return json;
    }

    public String getFromAgent() {
        return fromAgent;
    }

    public String getToAgent() {
        return toAgent;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }
}
