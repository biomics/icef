package org.coreasm.biomics.serializers;

import java.util.Set;
import java.util.Iterator;

import java.io.IOException;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.Update;
import org.coreasm.engine.absstorage.UpdateMultiset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class UpdateMultisetSerializer extends JsonSerializer<UpdateMultiset> {

    @Override
    public void serialize(UpdateMultiset set, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        System.out.println("Serialize UpdateMultiSet");

        jgen.writeStartObject();

        jgen.writeArrayFieldStart("updates");
        Iterator<Update> it = set.iterator();

        int counter = 0;
        while(it.hasNext()) {
            Update update = it.next();
            jgen.writeObject(update);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }
}
