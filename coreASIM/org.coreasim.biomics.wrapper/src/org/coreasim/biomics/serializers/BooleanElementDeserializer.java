/*
 * BooleanElementDeserializer.java v1.0
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

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class BooleanElementDeserializer extends JsonDeserializer<BooleanElement> {

    @Override
    public BooleanElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return BooleanElement.getInstance(node.get("value").asBoolean());
    }
}
