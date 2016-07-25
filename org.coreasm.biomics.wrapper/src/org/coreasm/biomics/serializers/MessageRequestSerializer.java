/*
 * MessageRequestSerializer.java v1.0
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

import org.coreasm.biomics.MessageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MessageRequestSerializer extends JsonSerializer<MessageRequest> {

    @Override
    public void serialize(MessageRequest req, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeStringField("type", req.type);
        jgen.writeStringField("simulation", req.simulation);
        if(req.toAgent != null)
            jgen.writeStringField("toAgent", req.toAgent);
        jgen.writeStringField("fromAgent", req.fromAgent);
        jgen.writeStringField("body", req.body);
        jgen.writeEndObject();
    }
}
