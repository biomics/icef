/*
 * LocationSerializer.java v1.0
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

import java.io.IOException;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.Location;

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
