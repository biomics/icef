package org.coreasm.biomics.serializers;

import java.io.IOException;

import org.coreasm.engine.absstorage.Update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class UpdateSerializer extends JsonSerializer<Update> {

    @Override
    public void serialize(Update update, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeObjectField("location", update.loc);
        jgen.writeObjectField("value", update.value);
        jgen.writeStringField("action", update.action);
        jgen.writeEndObject();
    }
}
