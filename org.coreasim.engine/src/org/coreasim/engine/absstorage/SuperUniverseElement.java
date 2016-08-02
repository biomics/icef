/*	
 * SuperUniverseElement.java 	1.0 	
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
 *	This class implements the super universe background.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
@Deprecated
public final class SuperUniverseElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1226100582613403135L;
	/**
	 * Name of the super universe background
	 */
	public static final String SUPER_UNIVERSE_NAME = "SUPER_UNIVERSE";

	/**
	 * Creates a new Super Universe background.
	 * The name of this background is defined by
	 * <code>SUPER_UNIVERSE_NAME</code>.
	 * 
	 * @see BackgroundElement#BackgroundElement()
	 */
	public SuperUniverseElement() {
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
