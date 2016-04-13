package org.coreasm.biomics;

import java.util.Set;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.plugins.set.SetElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class SetElementSerializer extends JsonSerializer<SetElement> {

    @Override
    public void serialize(SetElement set, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeArrayFieldStart("members");
        Set<Element> realSet = set.getSet();
        int counter = 0;
        for(Element e : realSet) {
            jgen.writeObject(e);
        }
        jgen.writeEndArray();
    }
    
    @Override 
    public void serializeWithType(SetElement set, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(set, jgen);
        serialize(set, jgen, provider);
        typeSer.writeTypeSuffixForObject(set, jgen);
    }
}
