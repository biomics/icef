/*
 * UpdateDeserializer.java v1.0
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
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.Element;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateDeserializer extends JsonDeserializer<Update> {

    @Override
    public Update deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);
        JsonNode jsonLocation = node.get("location");
        Location location = null;
        if(jsonLocation != null)
            location = mapper.treeToValue(jsonLocation, Location.class);

        JsonNode jsonValue = node.get("value");
        Element value = null;
        if(jsonValue != null)
            value = mapper.treeToValue(jsonValue, Element.class);

        JsonNode jsonAction = node.get("action");
        String action = "";
        if(jsonAction != null)
            action = jsonAction.textValue();

        return new Update(location, value, action, (Element)null, null);
    }
}
