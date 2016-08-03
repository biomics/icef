/*
 * RuleElementSerializer.java v1.0
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

import java.util.List;

import org.coreasim.engine.absstorage.RuleElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class RuleElementSerializer extends JsonSerializer<RuleElement> {

    @Override
    public void serialize(RuleElement rule, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStringField("name", rule.getName());
        jgen.writeStringField("decl", rule.getDeclarationNode().unparseTree());
        jgen.writeStringField("body", rule.getBody().unparseTree());

        jgen.writeArrayFieldStart("params");
        List<String> params = rule.getParam();
        for(String s : params) {
            jgen.writeString(s);
        }
        jgen.writeEndArray();
    }
    
    @Override 
    public void serializeWithType(RuleElement rule, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(rule, jgen);
        serialize(rule, jgen, provider);
        typeSer.writeTypeSuffixForObject(rule, jgen);
    }
}
