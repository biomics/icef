package org.coreasm.biomics.serializers;

import java.io.IOException;

import org.coreasm.engine.plugins.number.NumberElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class NumberElementSerializer extends JsonSerializer<NumberElement> {

    @Override
    public void serialize(NumberElement number, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeNumberField("value", number.getValue());
    }
    
    @Override 
    public void serializeWithType(NumberElement str, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(str, jgen);
        serialize(str, jgen, provider);
        typeSer.writeTypeSuffixForObject(str, jgen);
    }
}
