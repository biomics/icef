package org.coreasm.biomics.serializers;

import java.io.IOException;

import org.coreasm.biomics.MessageRequest;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class MessageRequestDeserializer extends JsonDeserializer<MessageRequest> {

    @Override
    public MessageRequest deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        JsonNode jsonTo = node.get("toAgent");
        String strTo = "";
        if(jsonTo != null)
            strTo = jsonTo.textValue();

        JsonNode jsonFrom = node.get("fromAgent");
        String strFrom = "";
        if(jsonFrom != null)
            strFrom = jsonFrom.textValue();

        JsonNode jsonBody = node.get("body");
        String strBody = "";
        if(jsonBody != null) {
            if(jsonBody.isTextual()) 
                strBody = jsonBody.textValue();
            else if(jsonBody.isObject()) {
                strBody = jsonBody.toString();
            }
        }

        JsonNode jsonType = node.get("type");
        String strType = "";
        if(jsonType != null) 
            strType = jsonType.textValue();

        /* MessageRequest msg = new MessageRequest();
        msg.toAgent = strTo;
        msg.fromAgent = strFrom;
        msg.type = strType;
        msg.body = strBody;
        return msg;*/
        return new MessageRequest(strType, strFrom, strTo, strBody);
    }
}
