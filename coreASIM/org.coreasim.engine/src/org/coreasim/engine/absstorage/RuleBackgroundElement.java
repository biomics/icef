/*	
 * RuleBackgroundElement.java 	1.0 	
 * 
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
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
 *	Class of Rule Background Element. There should only be
 *  one instance of this class in each state.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public class RuleBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2418684355552459853L;
	/**
	 * Name of the Rule background
	 */
	public static final String RULE_BACKGROUND_NAME = "RULE";

	/**
	 * Creates a new Rule background.
	 * 
	 * @see #RULE_BACKGROUND_NAME 
	 */
	public RuleBackgroundElement() {
		super();
	}

	/**
	 * Overriden to prevent creation of any new rule.
	 * 
	 * @see org.coreasim.engine.absstorage.BackgroundElement#getNewValue()
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	public Element getNewValue() {
		throw new UnsupportedOperationException("Cannot create new rule.");
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * Rule Elements.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(java.util.List)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof RuleElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

}
