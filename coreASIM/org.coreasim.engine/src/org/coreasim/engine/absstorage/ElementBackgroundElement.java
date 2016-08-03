/*	
 * ElementBackgroundElement.java  	1.0
 * 
 *
 * Copyright (C) 2005-2007 Roozbeh Farahbod 
 * 
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

/** 
 *	This is the background of all the elements in CoreASM.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public final class ElementBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -473221793936902711L;
	/**
	 * Name of the Element background
	 */
	public static final String ELEMENT_BACKGROUND_NAME = "ELEMENT";

	/**
	 * Creates a new Element background.
	 */
	public ElementBackgroundElement() {
		super();
	}

	/**
	 * Returns a new Element.
	 */
	@Override
	public Element getNewValue() {
		return new Element();
	}

	/** 
	 * Returns <code>TRUE</code> for any Element.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(java.util.List)
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return BooleanElement.TRUE;
	}

}
