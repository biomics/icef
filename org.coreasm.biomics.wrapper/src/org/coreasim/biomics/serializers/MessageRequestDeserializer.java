/*
 * MessageRequestDeserializer.java v1.0
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

import org.coreasim.biomics.MessageRequest;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class MessageRequestDeserializer extends JsonDeserializer<MessageRequest> {

    @Override
    public MessageRequest deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        JsonNode jsonSim = node.get("simulation");
        String simulation = "";
        if(jsonSim != null)
            simulation = jsonSim.textValue();

        JsonNode jsonTo = node.get("toAgent");
        String strTo = null;
        if(jsonTo != null)
            strTo = jsonTo.textValue();

        JsonNode jsonFrom = node.get("fromAgent");
        String strFrom = "";
        if(jsonFrom != null)
            strFrom = jsonFrom.textValue();

        JsonNode jsonBody = node.get("body");
        String strBody = "";
        if(jsonBody != null) {
            if(jsonBody.isTextual()) 
                strBody = jsonBody.textValue();
            else if(jsonBody.isObject()) {
                strBody = jsonBody.toString();
            }
        }

        JsonNode jsonType = node.get("type");
        String strType = "";
        if(jsonType != null) 
            strType = jsonType.textValue();

        return new MessageRequest(strType, simulation, strFrom, strTo, strBody);
    }
}
