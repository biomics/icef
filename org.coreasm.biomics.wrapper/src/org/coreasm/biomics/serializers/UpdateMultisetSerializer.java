/*
 * UpdateMultisetSerializer.java v1.0
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
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.Update;
import org.coreasm.engine.absstorage.UpdateMultiset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class UpdateMultisetSerializer extends JsonSerializer<UpdateMultiset> {

    @Override
    public void serialize(UpdateMultiset set, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeArrayFieldStart("updates");
        Iterator<Update> it = set.iterator();

        int counter = 0;
        while(it.hasNext()) {
            Update update = it.next();
            jgen.writeObject(update);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }
}
