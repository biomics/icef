package org.coreasm.biomics.serializers;

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

public class UpdateDeserializer extends JsonDeserializer<Update> {

    @Override
    public Update deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);
        JsonNode jsonLocation = node.get("location");
        Location location = null;
        if(jsonLocation != null)
            location = mapper.treeToValue(jsonLocation, Location.class);

        JsonNode jsonValue = node.get("value");
        Element value = null;
        if(jsonValue != null)
            value = mapper.treeToValue(jsonValue, Element.class);

        JsonNode jsonAction = node.get("action");
        String action = "";
        if(jsonAction != null)
            action = jsonAction.textValue();

        return new Update(location, value, action, (Element)null, null);
    }
}
