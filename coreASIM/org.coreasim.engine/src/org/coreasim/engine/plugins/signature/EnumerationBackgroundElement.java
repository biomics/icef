/*  
 * EnumerationBackgroundElement.java    1.0     04-Apr-2006
 * 
 *
 * Copyright (C) 2006 George Ma
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 * This file contains source code contributed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS) 
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.signature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.plugins.collection.AbstractSetElement;

/** 
 * This is the class of Enumeration Background elements. If an enumeration is
 * defined as:
 * <p>
 * <code><b>enum</b> MotorStates = {on, off}</code>
 * <p>
 * then an instnace of this class is created with the name "MotorStates" and
 * <i>on</i> and <i>off</i> as its members.
 *
 * @author  George Ma
 * 
 */
public class EnumerationBackgroundElement extends BackgroundElement
    implements Enumerable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -554599495674927156L;
	private final List<EnumerationElement> members;
    private List<Element> enumCache = null;
    
    public EnumerationBackgroundElement(List<EnumerationElement> members) {
        this.members = members;
        enumCache =  Collections.unmodifiableList(new ArrayList<Element>(members));
    }

    @Override
    public Element getNewValue() {
        return members.get(0);
    }

    @Override
    protected Element getValue(Element e) {
        return (members.contains(e)?BooleanElement.TRUE:BooleanElement.FALSE);
    }

    public Collection<Element> enumerate() {
    	return getIndexedView();
    }
    
    public boolean contains(Element e) {
        return enumerate().contains(e);
    }

	public List<Element> getIndexedView() throws UnsupportedOperationException {
		return enumCache;
	}

	public boolean supportsIndexedView() {
		return true;
	}

	public int size() {
		return enumCache.size();
	}

}
