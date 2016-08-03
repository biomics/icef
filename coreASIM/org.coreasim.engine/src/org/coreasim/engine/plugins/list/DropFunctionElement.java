/*	
 * DropFunctionElement.java  	1.0
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
 
package org.coreasim.engine.plugins.list;

import java.util.ArrayList;
import java.util.List;

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.plugins.collection.AbstractListElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Implementation of the 'drop(list, int)' function.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class DropFunctionElement extends TakeFunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8346041741484315624L;
	public static final String NAME = "drop";
	
	public DropFunctionElement(ControlAPI capi) {
		super(capi);
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		AbstractListElement list = (AbstractListElement)args.get(0);
		NumberElement n = (NumberElement)args.get(1);
		List<Element> resultValues = new ArrayList<Element>();

		int i = (int)n.getValue() + 1;
		while (i <= list.size()) {
			resultValues.add(list.get(NumberElement.getInstance(i)));
			i++;
		}
		
		return new ListElement(resultValues);
	}

}
