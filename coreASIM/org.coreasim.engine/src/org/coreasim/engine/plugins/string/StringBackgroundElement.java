/*	
 * StringBackgroundElement.java 	1.0 	1.0
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
 
package org.coreasim.engine.plugins.string;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

/** 
 *	Background of strings.
 *   
 *  @author  Mashaal Memon
 *  
 */
public class StringBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8767847510147089508L;
	/**
	 * Name of the String background
	 */
	public static final String STRING_BACKGROUND_NAME = "STRING";
	
	/**
	 * Creates a new String background.
	 * 
	 * @see #STRING_BACKGROUND_NAME 
	 */
	public StringBackgroundElement() {
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.BackgroundElement#getNewValue()
	 */
	@Override
	public Element getNewValue() {
		return new StringElement("");
		
		// The following line is removed by Roozbeh Farahbod, Aug 2006
		//throw new UnsupportedOperationException(
		//	"New string element cannot be returned without specifying the string that the element should represent.");
	}
	
	
	/**
	 * Return new string element representing specified string.
	 * 
	 * @param string A <code>String</code> representing string element requested.
	 * 
	 * @return a <code>StringElement</code> representing specified string.
	 */
	public StringElement getNewValue(String string) {
		return new StringElement(string);
	}

	/** 
	 * Returns a <code>TRUE</code> boolean for 
	 * String Elements. Otherwise <code>FALSE<code> is returned.
	 * 
	 * @see org.coreasim.engine.absstorage.AbstractUniverse#getValue(Element)
	 * @see BooleanElement
	 */
	@Override
	protected BooleanElement getValue(Element e) {
		return (e instanceof StringElement)?BooleanElement.TRUE:BooleanElement.FALSE;
	}

}
