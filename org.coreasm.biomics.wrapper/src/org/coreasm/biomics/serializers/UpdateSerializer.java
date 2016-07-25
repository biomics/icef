/*
 * UpdateSerializer.java v1.0
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

import java.io.IOException;

import org.coreasm.engine.absstorage.Update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class UpdateSerializer extends JsonSerializer<Update> {

    @Override
    public void serialize(Update update, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeObjectField("location", update.loc);
        jgen.writeObjectField("value", update.value);
        jgen.writeStringField("action", update.action);
        jgen.writeEndObject();
    }
}
