package org.coreasm.biomics.serializers;

import java.util.ArrayList;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Update;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.Element;

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
