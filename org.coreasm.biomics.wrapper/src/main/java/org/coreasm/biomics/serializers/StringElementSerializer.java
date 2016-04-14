package org.coreasm.biomics;

import java.io.IOException;

import org.coreasm.engine.plugins.string.StringElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class StringElementSerializer extends JsonSerializer<StringElement> {

    @Override
    public void serialize(StringElement str, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStringField("string", str.toString());
    }
    
    @Override 
    public void serializeWithType(StringElement str, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(str, jgen);
        serialize(str, jgen, provider);
        typeSer.writeTypeSuffixForObject(str, jgen);
    }
}
