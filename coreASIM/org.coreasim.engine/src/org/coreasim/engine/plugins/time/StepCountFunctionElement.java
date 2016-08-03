/*	
 * StepCountFunctionElement.java 	1.0 	$Revision: 237 $
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

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Provides a monitored function that returns current step count.
 *   
 * @author  Roozbeh Farahbod
 */
public class StepCountFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2811059378375548047L;

	/** Name of this function */
	public static final String FUNC_NAME = "stepcount";
	
	private final ControlAPI capi;
	
	public StepCountFunctionElement(ControlAPI capi) {
		super();
		setFClass(FunctionClass.fcMonitored);
		this.capi = capi;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() > 0) 
			return Element.UNDEF;
		else
			return NumberElement.getInstance(capi.getStepCount());
	}

}
