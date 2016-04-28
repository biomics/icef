package org.coreasm.biomics.serializers;

import java.util.ArrayList;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.plugins.list.ListElement;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ListElementDeserializer extends JsonDeserializer<ListElement> {

    @Override
    public ListElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);
        JsonNode membersNode = node.get("members");

        ArrayList<Element> realList = new ArrayList<Element>();
        if(membersNode != null && membersNode.isArray()) {
            Iterator<JsonNode> it = membersNode.elements();
            while(it.hasNext()) {
                JsonNode n = it.next();
                Element e = mapper.treeToValue(n, Element.class);
                realList.add(e);
            }
        }
       
        return new ListElement(realList);
    }
}
