/*	
 * SetBackgroundElement.java 	1.0 	
 * 
 *
 * Copyright (C) 2006 Mashaal Memon
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

package org.coreasim.engine.plugins.set;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

/**
 * An abstract class that implements the SET background Element.
 * 
 * @author Mashaal Memon
 * 
 */
public class SetBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8526872175973915872L;
	/**
	 * Name of the set background
	 */
	public static final String SET_BACKGROUND_NAME = "SET";
	
	/**
	 * Creates a new Set background.
	 * 
	 * @see #SET_BACKGROUND_NAME 
	 */
	public SetBackgroundElement() {
		super();
	}
	
	/**
	 * Returns a new set element with no set members.
	 * 
	 * @return <code>Element<code> which is actually an empty set in the simulated machine.
	 * 
	 * @see org.coreasim.engine.absstorage.BackgroundElement#getNewValue()
	 */
	@Override
	public Element getNewValue() {
		return new SetElement();
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * Set Elements. Otherwise <code>FALSE<code> is returned.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(Element)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof SetElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

}
