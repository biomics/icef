package org.coreasm.biomics.serializers;

import java.io.IOException;

import org.coreasm.engine.plugins.number.NumberElement;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class NumberElementDeserializer extends JsonDeserializer<NumberElement> {

    @Override
    public NumberElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        return NumberElement.getInstance(node.get("value").asLong());
    }
}
