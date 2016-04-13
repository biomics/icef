package org.coreasm.biomics;

import java.util.HashSet;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.plugins.set.SetElement;

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
