/*	
 * BackgroundElement.java 	1.0 	
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

/**
 * An abstract class that implements the BACKGROUND Element.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public abstract class BackgroundElement extends AbstractUniverse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9140537629799958095L;

	/**
	 * Creates a new background element. The function class of this
	 * background is set to <code>FunctionClass.fcStatic</code>.
	 * 
	 * @see AbstractUniverse#AbstractUniverse()
	 * @see FunctionElement.FunctionClass
	 */
	public BackgroundElement() {
		super.setFClass(FunctionClass.fcStatic);
	}

	/**
	 * Overrides the <code>setFClass</code> function to prevent changing the
	 * function class of Backgrounds.
	 * 
	 * @see org.coreasim.engine.absstorage.FunctionElement#setFClass(org.coreasim.engine.absstorage.FunctionElement.FunctionClass)
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public final void setFClass(FunctionClass fClass) {
		throw new UnsupportedOperationException(
				"Function class of backgrounds cannot be changed.");
	}

	/**
	 * Returns a possibly virtual new value of this background.
	 * 
	 * @return and Element as a new value from this background
	 */
	public abstract Element getNewValue();

	/**
	 * If this method is supported by this background,
	 * it returns an element of this background that 
	 * is represented by the given string value. 
	 * 
	 * @param denotation the string representation of the element
	 * @return an element of this background that is represented by the given string value
	 * 
	 * @throws ElementFormatException if the conversion fails
	 * @throws UnsupportedOperationException if this operation is not supported by this background 
	 */
	public Element valueOf(String denotation) throws ElementFormatException {
		throw new UnsupportedOperationException("This background cannot parse values from a String representation.");
	}
	
	/*
	 * Returns the suggested name of this background
	 * which must be the name under which this background
	 * is registered in the state.
	 *
	public abstract String getBackgroundName();
	*/
	public String toString() {
		return "background-element";
	}
}
