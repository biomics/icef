/*	
 * StringLengthFunctionElement.java 	1.0 	
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
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
 */
 
package org.coreasim.engine.plugins.string;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Computes the length of a string element.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class StringLengthFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7903213564062655390L;
	/** name of the 'strlen' function */
	public static final String STRLENGTH_FUNC_NAME = "strlen";
	
	/**
	 * Creates a derived function.
	 */
	public StringLengthFunctionElement() {
		this.setFClass(FunctionClass.fcDerived);
	}

	/**
	 * @return the length of the string element, if the argument is a list of one 
	 * string element; otherwise, returns <code>Element.UNDEF</code>.
	 * 
	 * @see Element#UNDEF
	 * @see StringElement
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 1) {
			Element str = args.get(0);
			if (str instanceof StringElement)
				return NumberElement.getInstance(((StringElement)str).toString().length());
		}
		
		return Element.UNDEF;
	}

}
