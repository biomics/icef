/*	
 * RuleBackgroundElement.java 	1.0 	$Revision: 243 $
 * 
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
 * 
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.absstorage;

/** 
 *	Class of Policy Background Element. There should only be
 *  one instance of this class in each state.
 *   
 *  @author  Eric Rothstein
 *  
 */
public class PolicyBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1102975977060511238L;
	/**
	 * Name of the Policy background
	 */
	public static final String POLICY_BACKGROUND_NAME = "POLICY";

	/**
	 * Creates a new Policy background.
	 * 
	 * @see #POLICY_BACKGROUND_NAME 
	 */
	public PolicyBackgroundElement() {
		super();
	}

	/**
	 * Overriden to prevent creation of any new policy.
	 * 
	 * @see org.coreasim.engine.absstorage.BackgroundElement#getNewValue()
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	public Element getNewValue() {
		throw new UnsupportedOperationException("Cannot create new policy.");
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * Policy Elements.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(java.util.List)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof PolicyElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

}
