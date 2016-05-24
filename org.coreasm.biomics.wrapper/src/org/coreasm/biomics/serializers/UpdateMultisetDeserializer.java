package org.coreasm.biomics.serializers;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Update;
import org.coreasm.engine.absstorage.UpdateMultiset;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateMultisetDeserializer extends JsonDeserializer<UpdateMultiset> {

    @Override
    public UpdateMultiset deserialize(JsonParser jsonParser, DeserializationContext context) 
        throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        ObjectMapper mapper = (ObjectMapper) oc;

        JsonNode node = oc.readTree(jsonParser);

        JsonNode updatesNode = node.get("updates");

        Set<Update> s = new HashSet<Update>();
        if(updatesNode != null && updatesNode.isArray()) {
            Iterator<JsonNode> it = updatesNode.elements();
            while(it.hasNext()) {
                JsonNode n = it.next();
                Update u = mapper.treeToValue(n, Update.class);
                s.add(u);
            }
        }

        return new UpdateMultiset(s);
    }
}
