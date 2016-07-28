/*
 * MessageElementDeserializer.java v1.0
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

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.plugins.string.StringElement;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class MessageElementDeserializer extends JsonDeserializer<MessageElement> {

    @Override
    public MessageElement deserialize(JsonParser jsonParser, DeserializationContext context) 
        throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("type").textValue();
        String from = node.get("fromAgent").textValue();
        String to = node.get("toAgent").textValue();
        String subject = node.get("subject").textValue();

        Element e = null;
        if(type.equals("StringElement")) {
            System.out.println("Create Stringelement");
            // e = new StringElement(node.get("message").get("string").getTextValue());
            e = deserialize(jsonParser, context);
        }

        return new MessageElement(from, e, to, subject, 1, type);
    }
}
