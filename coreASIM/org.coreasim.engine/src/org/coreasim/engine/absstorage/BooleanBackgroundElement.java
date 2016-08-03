/*	
 * BooleanBackgroundElement.java 	1.0 	
 * 
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
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
 *
 */
 
package org.coreasim.engine.absstorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** 
 *	Class of Boolean Background Element. There should only be
 *  one instance of this class in each state.
 *   
 *  @author  Roozbeh Farahbod
 *   
 */
public class BooleanBackgroundElement extends BackgroundElement 
    implements Enumerable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2851780538964245314L;

	/**
	 * Name of the boolean background
	 */
	public static final String BOOLEAN_BACKGROUND_NAME = "BOOLEAN";

	private final List<Element> enumeration;
	
	/**
	 * Creates a new Boolean background.
	 * 
	 * @see #BOOLEAN_BACKGROUND_NAME 
	 */
	public BooleanBackgroundElement() {
		super();
        List<Element> e = new ArrayList<Element>();
        e.add(BooleanElement.TRUE);
        e.add(BooleanElement.FALSE);
        enumeration = Collections.unmodifiableList(e);
	}

	/**
	 * Returns a <code>FALSE</code> Boolean Element.
	 * 
	 * @see org.coreasim.engine.absstorage.BackgroundElement#getNewValue()
	 */
	@Override
	public Element getNewValue() {
		return BooleanElement.FALSE;
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * Boolean Elements.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(Element)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof BooleanElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

    public Collection<Element> enumerate() {
    	return enumeration;
    }

	public boolean contains(Element e) {
		return (e.equals(BooleanElement.TRUE) || e.equals(BooleanElement.FALSE));
	}

	public List<Element> getIndexedView() throws UnsupportedOperationException {
		return enumeration;
	}

	public boolean supportsIndexedView() {
		return true;
	}

	public int size() {
		return 2;
	}

}
