/*
 * SetElementDeserializer.java v1.0
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

import java.util.HashSet;
import java.util.Iterator;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.plugins.set.SetElement;

import java.io.IOException;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SetElementDeserializer extends JsonDeserializer<SetElement> {

    @Override
    public SetElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);
        JsonNode membersNode = node.get("members");

        HashSet<Element> realSet = new HashSet<Element>();
        if(membersNode != null && membersNode.isArray()) {
            Iterator<JsonNode> it = membersNode.elements();
            while(it.hasNext()) {
                JsonNode n = it.next();
                Element e = mapper.treeToValue(n, Element.class);
                realSet.add(e);
            }
        }
       
        return new SetElement(realSet);
    }
}
