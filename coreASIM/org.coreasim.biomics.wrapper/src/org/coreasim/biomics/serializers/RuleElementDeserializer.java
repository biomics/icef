/*
 * RuleElementDeserializer.java v1.0
 *
 * This file contains source code developed by the European
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 *
 */

package org.coreasim.biomics.serializers;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import org.coreasim.engine.Engine;
import org.coreasim.engine.absstorage.RuleElement;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.parser.Parser;
import org.coreasim.engine.parser.ParserException;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class RuleElementDeserializer extends JsonDeserializer<RuleElement> {

    private final Engine engine;

    public RuleElementDeserializer(Engine engine) {
        super();
        this.engine = engine;
    }

    @Override
    public RuleElement deserialize(JsonParser jsonParser, DeserializationContext context) 
    throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String name = "";
        JsonNode jsonName = node.get("name");
        if(name != null) {
            name = jsonName.textValue();
        }
        
        ASTNode declNode = null;
        JsonNode jsonDecl = node.get("decl");
        if(jsonDecl != null) {
            String strDecl = jsonDecl.textValue();
            try {
                Parser p = engine.getParser();
                declNode = p.parseRuleDeclarationOnly(strDecl);
            } catch (ParserException e) {
                System.err.println("ERROR: Unable to parse declaration of rule!");
                System.err.println(e);
            }
        }

        ASTNode bodyNode = null;
        JsonNode jsonBody = node.get("body");
        if(jsonBody != null) {
            String strBody = jsonBody.textValue();
        
            try {
                bodyNode = engine.getParser().parseRuleOnly(strBody);
            } catch (ParserException e) {
                System.err.println("ERROR: Unable to parse body of rule!");
                System.err.println(e);
            }
        }

        ArrayList<String> paramList = new ArrayList<String>();
        JsonNode jsonParams = node.get("params");
        if(jsonParams != null && jsonParams.isArray()) {
            Iterator<JsonNode> it = jsonParams.elements();
            while(it.hasNext()) {
                JsonNode jsonString = it.next();
                if(jsonString != null) {
                    paramList.add(jsonString.textValue());
                }
            }
        }

        return new RuleElement(declNode, name, paramList, bodyNode);
    }
}


// - Engine has Parser
// - Parser get initialized by setSpecification
// - Method parseSpecification can parse a rule alone?
