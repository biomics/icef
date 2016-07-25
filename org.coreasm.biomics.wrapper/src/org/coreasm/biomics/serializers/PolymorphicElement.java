/*
 * PolyMorphicElement.java v1.0
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

package org.coreasm.biomics.serializers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.absstorage.RuleElement;
import org.coreasm.engine.plugins.string.StringElement;
import org.coreasm.engine.plugins.number.NumberElement;
import org.coreasm.engine.plugins.set.SetElement;
import org.coreasm.engine.plugins.list.ListElement;
import org.coreasm.engine.plugins.map.MapElement;
import org.coreasm.engine.plugins.signature.EnumerationElement;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
            @Type(value = MessageElement.class, name="MessageElement"),
            @Type(value = StringElement.class, name="StringElement"),
            @Type(value = NumberElement.class, name="NumberElement"),
            @Type(value = SetElement.class, name="SetElement"),
            @Type(value = ListElement.class, name="ListElement"),
            @Type(value = MapElement.class, name="MapElement"),
            @Type(value = EnumerationElement.class, name="EnumerationElement"),
            @Type(value = RuleElement.class, name="RuleElement"),
    })
public abstract class PolymorphicElement {
    // Intentionally empty class
}
