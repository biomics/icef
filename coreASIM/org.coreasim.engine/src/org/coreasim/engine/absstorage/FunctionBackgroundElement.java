/*	
 * FunctionBackgroundElement.java 	1.0 	
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


/** 
 *	Class of Function Background Element. There should only be
 *  one instance of this class in each state.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public class FunctionBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -744590362010379503L;
	/**
	 * Name of the function background
	 */
	public static final String FUNCTION_BACKGROUND_NAME = "FUNCTION";

	/**
	 * Creates a new Function background.
	 * 
	 * @see #FUNCTION_BACKGROUND_NAME 
	 */
	public FunctionBackgroundElement() {
		super();
	}

	/**
	 * Overriden to prevent creation of any new function.
	 * 
	 * @see org.coreasim.engine.absstorage.BackgroundElement#getNewValue()
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	public Element getNewValue() {
		throw new UnsupportedOperationException("Cannot create new function.");
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * Function Elements.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(java.util.List)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof FunctionElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

}
