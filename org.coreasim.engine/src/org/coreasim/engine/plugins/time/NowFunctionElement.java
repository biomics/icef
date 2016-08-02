/*	
 * NowFunctionElement.java 	1.0 	1.0
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
 
package org.coreasim.engine.plugins.time;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Implements 'now' as a monitored function that returns current time in milliseconds.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class NowFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2910086078546575439L;
	/** Name of this function */
	public static final String NOW_FUNC_NAME = "now";
	
	public NowFunctionElement() {
		super();
		setFClass(FunctionClass.fcMonitored);
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() > 0) 
			return Element.UNDEF;
		else
			return NumberElement.getInstance(System.currentTimeMillis());
	}

}
