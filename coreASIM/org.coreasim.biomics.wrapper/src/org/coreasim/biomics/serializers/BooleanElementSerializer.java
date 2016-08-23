/*
 * BooleanElementSerializer.java v1.0
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

import org.coreasim.engine.absstorage.BooleanElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class BooleanElementSerializer extends JsonSerializer<BooleanElement> {

    @Override
    public void serialize(BooleanElement b, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeBooleanField("value", b.getValue());
    }
    
    @Override 
    public void serializeWithType(BooleanElement str, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {
        
        typeSer.writeTypePrefixForObject(str, jgen);
        serialize(str, jgen, provider);
        typeSer.writeTypeSuffixForObject(str, jgen);
    }
}
