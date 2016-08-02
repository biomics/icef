/*	
 * ToStringFunctionElement.java 	1.0 	$Revision: 243 $
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
 
package org.coreasim.engine.plugins.string;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;

/** 
 * Provides a String Element equivalent of elements.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class ToStringFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7734181913100018268L;
	/** name of the 'toString' function */
	public static final String TOSTRING_FUNC_NAME = "toString";
	
	public ToStringFunctionElement() {
		this.setFClass(FunctionClass.fcDerived);
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 1) {
			return new StringElement(args.get(0).toString());
		} else
			return Element.UNDEF;
	}

}
