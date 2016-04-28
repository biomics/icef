package org.coreasm.biomics.serializers;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.plugins.string.StringElement;

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
