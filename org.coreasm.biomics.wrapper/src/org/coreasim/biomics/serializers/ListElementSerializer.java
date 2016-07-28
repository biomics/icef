/*
 * ListElementSerializer.java v1.0
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

import java.util.List;
import java.util.ListIterator;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.plugins.list.ListElement;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class ListElementSerializer extends JsonSerializer<ListElement> {

    @Override
    public void serialize(ListElement list, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeArrayFieldStart("members");
        List<? extends Element> realList = list.getList();
        ListIterator<? extends Element> it = realList.listIterator();
        while(it.hasNext()) {
            jgen.writeObject(it.next());
        }
        jgen.writeEndArray();
    }
    
    @Override 
    public void serializeWithType(ListElement list, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(list, jgen);
        serialize(list, jgen, provider);
        typeSer.writeTypeSuffixForObject(list, jgen);
    }
}
