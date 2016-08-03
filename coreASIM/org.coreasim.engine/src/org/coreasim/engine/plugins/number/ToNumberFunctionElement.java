/*	
 * ToNumberFunctionElement.java 	1.0 	
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.number;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;

/** 
 * Provides a Number Element equivalent of elements.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class ToNumberFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1190164168214278385L;
	/** name of the 'toNumber' function */
	public static final String TONUMBER_FUNC_NAME = "toNumber";
	
	public ToNumberFunctionElement() {
		this.setFClass(FunctionClass.fcDerived);
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 1) {
			String arg = args.get(0).toString();
			Double d = null;
			try {
				d = Double.valueOf(arg);
				return NumberElement.getInstance(d);
			} catch (NumberFormatException e) {
				return Element.UNDEF;
			}
		} else
			return Element.UNDEF;
	}

}
