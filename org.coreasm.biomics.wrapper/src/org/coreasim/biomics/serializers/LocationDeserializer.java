/*
 * LocationDeserializer.java v1.0
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

import java.util.ArrayList;
import java.util.Iterator;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.Update;

import java.io.IOException;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationDeserializer extends JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);
        JsonNode jsonName = node.get("name");
        String name = "";
        if(jsonName != null)
            name = jsonName.textValue();

        JsonNode jsonArgs = node.get("args");

        ArrayList<Element> realList = new ArrayList<Element>();
        if(jsonArgs != null && jsonArgs.isArray()) {
            Iterator<JsonNode> it = jsonArgs.elements();
            while(it.hasNext()) {
                JsonNode n = it.next();
                Element e = mapper.treeToValue(n, Element.class);
                realList.add(e);
            }
        }
        
        return new Location(name, realList);
    }
}
