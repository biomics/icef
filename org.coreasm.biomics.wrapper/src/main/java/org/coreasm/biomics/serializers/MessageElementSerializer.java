package org.coreasm.biomics;

import java.io.IOException;

import org.coreasm.engine.absstorage.MessageElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MessageElementSerializer extends JsonSerializer<MessageElement> {

    @Override
    public void serialize(MessageElement msg, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStringField("fromAgent", msg.getFromAgent());
        jgen.writeObjectField("message", msg.getMessage());
        jgen.writeStringField("toAgent", msg.getToAgent());
        jgen.writeStringField("subject", msg.getSubject());
        jgen.writeStringField("type", msg.getType());
        jgen.writeNumberField("stepcount", msg.getStepcount());
    }

    @Override 
    public void serializeWithType(MessageElement msg, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(msg, jgen);
        serialize(msg, jgen, provider);
        typeSer.writeTypeSuffixForObject(msg, jgen);
    }

}
