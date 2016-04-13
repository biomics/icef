package org.coreasm.biomics;

import java.io.IOException;

import org.coreasm.engine.plugins.string.StringElement;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class StringElementDeserializer extends JsonDeserializer<StringElement> {

    @Override
    public StringElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return new StringElement(node.get("string").textValue());
    }
}
