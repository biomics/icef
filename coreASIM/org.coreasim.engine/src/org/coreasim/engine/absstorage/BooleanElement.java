/*	
 * BooleanElement.java 	1.0 	$Revision: 243 $
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
 *	This implements the Boolean Element.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public class BooleanElement extends Element {

  	/**
	 * 
	 */
	private static final long serialVersionUID = 1024552280543337148L;

	/**
 	 * Name of the 'true' value.
 	 */
 	public static final String TRUE_NAME = "true";

 	/**
 	 * Name of the 'false' value.
 	 */
 	public static final String FALSE_NAME = "false";

 	/**
 	 * Holds the value of this Boolean Element as a 
 	 * Java boolean value 
 	 */
 	private Boolean value;
 	
 	/**
 	 * Represents the 'true' value in ASM.
 	 */
 	public static final BooleanElement TRUE = new BooleanElement(true);
 	
 	/**
 	 * Represents the 'flase' value in ASM.
 	 */
 	public static final BooleanElement FALSE = new BooleanElement(false);
 	
	/**
	 * Returns a Boolean Element of the given boolean value
	 */
	public static BooleanElement valueOf(boolean value) {
		return value?TRUE:FALSE;
	}

    public synchronized static BooleanElement getInstance(boolean v) {
		return new BooleanElement(v);
	}

	/**
	 * A private constructor to creates a new Boolean value.
	 * 
	 * @param value the boolean value of this Element
	 */
	private BooleanElement(boolean value) {
		super();
		this.value = value;
	}

	public String getBackground() {
		return BooleanBackgroundElement.BOOLEAN_BACKGROUND_NAME;
	}
	
	/**
	 * Returns the Java boolean value of this 
	 * Boolean Element 
	 */
	public boolean getValue() {
		return value;
	}
	
	/**
	 * Returns a <code>String</code> representation of 
	 * this Boolean Element.
	 * 
	 * @see org.coreasim.engine.absstorage.Element#toString()
	 */
	@Override
	public String toString() {
		return value?TRUE_NAME:FALSE_NAME;
	}

	@Override
	public boolean equals(Object anElement) {
		if (super.equals(anElement))
			return true;
		else 
			if (anElement instanceof BooleanElement) {
				return this.value == ((BooleanElement)anElement).value;
			} else
				return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
}
