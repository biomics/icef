/*	
 * FunctionRangeFunctionElement.java 	1.0
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.signature;

import java.util.List;
import java.util.Set;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.plugins.set.SetElement;

/** 
 * The 'range(f)' function that returns the range of a function
 * in form of a set of elements.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class FunctionRangeFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7098417622428801883L;
	/**
	 * Suggested name of this function
	 */
	public static final String FUNCTION_NAME = "range";
	
//	private ControlAPI capi;
	
	public FunctionRangeFunctionElement() {
		setFClass(FunctionClass.fcDerived);
//		this.capi = capi;
	}
	
	/**
	 * If the args is a list of only one function element, this method
	 * returns a {@link SetElement set} of the values of the function element. Otherwise, returns
	 * {@link Element#UNDEF undef}.
	 * 
	 * @see org.coreasim.engine.absstorage.FunctionElement#getValue(java.util.List)
	 * @see SetElement
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 1) {
			Element e = args.get(0);
			if (e instanceof FunctionElement) {
				// Here it would be better if we used the SetBackground
				// to get a new value, but it doesn't matter for sets.
				Set<? extends Element> elements = ((FunctionElement)e).getRange();
				return new SetElement(elements);
			}
		} 
		return Element.UNDEF;
	}

}
