/*
 * SetElementSerializer.java v1.0
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

package org.coreasm.biomics.serializers;

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
