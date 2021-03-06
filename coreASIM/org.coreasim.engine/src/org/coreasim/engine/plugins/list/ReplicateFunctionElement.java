/*
 * ReplicateFunctionElement.java
 *  
 * Copyright (C) 2006-2016 The CoreASM Team
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
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
 * Function element providing the 'replicate' function.
 *   
 * @author Michael Stegmaier
 * 
 */
public class ReplicateFunctionElement extends ListFunctionElement {

	/** suggested name for this function */
	public static final String NAME = "replicate";

	protected Signature signature;

	public ReplicateFunctionElement(ControlAPI capi) {
		super(capi);
		signature = new Signature();
		signature.setDomain(
				ElementBackgroundElement.ELEMENT_BACKGROUND_NAME,
				NumberBackgroundElement.NUMBER_BACKGROUND_NAME);
		signature.setRange(ListBackgroundElement.LIST_BACKGROUND_NAME);
	}

	@Override
	public Signature getSignature() {
		return signature;
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		NumberElement n = (NumberElement)args.get(1);
		return new ListElement(replicate(args.get(0), n));
	}

	/**
	 * Implementation of a Haskell like replicate function
	 * @param x element to replicate
	 * @param n number of desired replications
	 * @return list
	 */
	private List<? extends Element> replicate(Element x, NumberElement n) {
		ArrayList<Element> result = new ArrayList<Element>();
		for (int i = 0; i < n.getValue(); i++)
			result.add(x);
		return result;
	}

	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 2)
				&& (args.get(1) != null && args.get(1) instanceof NumberElement && ((NumberElement)args.get(1)).isInteger() && ((NumberElement)args.get(1)).getValue() >= 0);
	}
}
