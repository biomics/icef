package org.coreasm.biomics;

import java.io.IOException;

import org.coreasm.engine.plugins.signature.EnumerationElement;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class EnumerationElementDeserializer extends JsonDeserializer<EnumerationElement> {

    @Override
    public EnumerationElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        EnumerationElement e = new EnumerationElement(node.get("name").textValue());
        JsonNode bkg = node.get("bkg");

        return e;
    }
}
