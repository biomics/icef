package org.coreasm.biomics.serializers;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.ElementList;
import org.coreasm.engine.absstorage.Location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class LocationSerializer extends JsonSerializer<Location> {

    @Override
    public void serialize(Location loc, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeStringField("name", loc.name);
        jgen.writeArrayFieldStart("args");
        ElementList args = loc.args;
        for(Element e : args) {
            jgen.writeObject(e);
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
    
    @Override 
    public void serializeWithType(Location loc, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(loc, jgen);
        serialize(loc, jgen, provider);
        typeSer.writeTypeSuffixForObject(loc, jgen);
    }
}
