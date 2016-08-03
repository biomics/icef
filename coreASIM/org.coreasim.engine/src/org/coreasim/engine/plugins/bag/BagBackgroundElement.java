/*	
 * BagBackgroundElement.java  	1.0
 * 
 * Copyright (C) 2008 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.bag;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

/** 
 * Background of bags.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class BagBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 446152692672546264L;
	/**
	 * Name of the bag background
	 */
	public static final String BAG_BACKGROUND_NAME = "BAG";
	
	/**
	 * Creates a new Bag background.
	 * 
	 * @see #BAG_BACKGROUND_NAME 
	 */
	public BagBackgroundElement() {
		super();
	}
	
	/**
	 * Returns a new Bag element with no members.
	 * 
	 * @return <code>Element<code> which is actually an empty bag in the simulated machine.
	 * 
	 * @see org.coreasim.engine.absstorage.BackgroundElement#getNewValue()
	 */
	@Override
	public Element getNewValue() {
		return new BagElement();
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * Bag Elements. Otherwise <code>FALSE<code> is returned.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(Element)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof BagElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

}
