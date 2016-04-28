package org.coreasm.biomics.serializers;

import java.util.HashMap;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.plugins.map.MapElement;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapElementDeserializer extends JsonDeserializer<MapElement> {

    @Override
    public MapElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);
        JsonNode membersNode = node.get("members");

        HashMap<Element, Element> realMap = new HashMap<Element, Element>();

        if(membersNode != null && membersNode.isArray()) {
            Iterator<JsonNode> it = membersNode.elements();

            while(it.hasNext()) {
                JsonNode n = it.next();
                Element key = mapper.treeToValue(n.get("key"), Element.class);
                Element value = mapper.treeToValue(n.get("value"), Element.class);
                realMap.put(key, value);
            }
        }
       
        return new MapElement(realMap);
    }
}
