/*
 * UpdateMultisetDeserializer.java v1.0
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

import java.util.Set;

import org.coreasim.engine.absstorage.Update;
import org.coreasim.engine.absstorage.UpdateMultiset;

import java.util.HashSet;
import java.util.Iterator;

import java.io.IOException;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateMultisetDeserializer extends JsonDeserializer<UpdateMultiset> {

    @Override
    public UpdateMultiset deserialize(JsonParser jsonParser, DeserializationContext context) 
        throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);

        JsonNode updatesNode = node.get("updates");

        Set<Update> s = new HashSet<Update>();
        if(updatesNode != null && updatesNode.isArray()) {
            Iterator<JsonNode> it = updatesNode.elements();
            while(it.hasNext()) {
                JsonNode n = it.next();
                Update u = mapper.treeToValue(n, Update.class);
                s.add(u);
            }
        }

        return new UpdateMultiset(s);
    }
}
