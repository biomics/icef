package org.coreasm.biomics;

import java.io.IOException;

import org.coreasm.biomics.MessageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MessageRequestSerializer extends JsonSerializer<MessageRequest> {

    @Override
    public void serialize(MessageRequest req, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeStringField("type", req.type);
        jgen.writeStringField("toAgent", req.toAgent);
        jgen.writeStringField("fromAgent", req.fromAgent);
        jgen.writeStringField("body", req.body);
        jgen.writeEndObject();
    }
}
