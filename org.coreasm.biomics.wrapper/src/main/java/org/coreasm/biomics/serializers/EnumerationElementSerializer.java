package org.coreasm.biomics;

import java.io.IOException;

import org.coreasm.engine.plugins.signature.EnumerationElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class EnumerationElementSerializer extends JsonSerializer<EnumerationElement> {

    @Override
    public void serialize(EnumerationElement enumE, JsonGenerator jgen, SerializerProvider provider) 
        throws IOException, JsonProcessingException {

        jgen.writeStringField("name", enumE.getName());
        String bkgName = enumE.getBackground();
        if(bkgName != null)
            jgen.writeStringField("bkg", bkgName);
    }
    
    @Override 
    public void serializeWithType(EnumerationElement enumE, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException, JsonProcessingException {

        typeSer.writeTypePrefixForObject(enumE, jgen);
        serialize(enumE, jgen, provider);
        typeSer.writeTypeSuffixForObject(enumE, jgen);
    }
}
