/*	
 * SetNthFunctionElement.java  	
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
import org.coreasim.engine.absstorage.ElementBackgroundElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.number.NumberBackgroundElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Implementation of the 'setnth' function which sets a new 
 * value at a specific index in the list and returns a new list.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class SetNthFunctionElement extends ListFunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1054569278091825082L;

	public static final String NAME = "setnth";
	
	protected Signature signature;
	
	public SetNthFunctionElement(ControlAPI capi) {
		super(capi);
		setFClass(FunctionClass.fcDerived);
		signature = new Signature();
		signature.setDomain(
				ListBackgroundElement.LIST_BACKGROUND_NAME,
				NumberBackgroundElement.NUMBER_BACKGROUND_NAME,
				ElementBackgroundElement.ELEMENT_BACKGROUND_NAME);
		signature.setRange(ListBackgroundElement.LIST_BACKGROUND_NAME);
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		String exception ="";
		if (args.size() != 3)
			exception = "Illegal number of arguments. "+NAME+" requires three arguments";
		else if (!(args.get(0) instanceof ListElement))
			exception = "Illegal argument: First argument of "+NAME+" must be a list.";
		else if (!(args.get(1) instanceof NumberElement))
			exception = "Illegal argument: Second argument of "+NAME+" must be a positive number.";
		else if (args.get(2) == null)
			exception = "Illegal argument: Thrid argument of " + NAME + " should be an element.";
		if (!exception.isEmpty())
			throw new CoreASIMError(exception);
		
		ListElement list = (ListElement) args.get(0);
		NumberElement n = (NumberElement)args.get(1);
		if (n.getValue() <= 0)
			throw new CoreASIMError("Index out of range for " + NAME + ". Second parameter must be a positive number.");
		else if (n.getValue() > list.size())
			throw new CoreASIMError(
					"Index out of range for " + NAME
							+ ". Second parameter must be a number which is at most the number of list elements.");

		List<Element> resultValues = new ArrayList<Element>();
		for (Element e: list.enumerate()) 
			resultValues.add(e);
		resultValues.set((int)n.getValue()-1, args.get(2));
		
		return new ListElement(resultValues);
	}

	@Override
	public Signature getSignature() {
		return signature;
	}
}

