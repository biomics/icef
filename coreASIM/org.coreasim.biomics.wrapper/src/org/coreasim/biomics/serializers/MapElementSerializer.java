/*
 * MapElementSerializer.java v1.0
 *
 * This file contains source code developed by the European
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 *
 */

package org.coreasim.biomics.serializers;

import java.util.Set;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.plugins.map.MapElement;

import java.util.List;
import java.util.Iterator;

import java.io.IOException;

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
