package org.coreasm.biomics;

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
