package org.coreasm.biomics.serializers;

import java.util.Set;
import java.util.List;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.plugins.map.MapElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MapElementSerializer extends JsonSerializer<MapElement> {

    @Override
    public void serialize(MapElement map, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeArrayFieldStart("members");
        Set<Element> keys = map.keySet();
        Iterator<Element> it = keys.iterator();

        int counter = 0;
        while(it.hasNext()) {
            Element key = it.next();

            jgen.writeStartObject();

            jgen.writeObjectField("key", key);
            jgen.writeObjectField("value", map.get(key));

            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
    
    @Override 
    public void serializeWithType(MapElement map, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(map, jgen);
        serialize(map, jgen, provider);
        typeSer.writeTypeSuffixForObject(map, jgen);
    }
}
