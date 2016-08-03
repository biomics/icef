/*	
 * NthFunctionElement.java  	1.0
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


import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementBackgroundElement;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.collection.AbstractListElement;
import org.coreasim.engine.plugins.number.NumberBackgroundElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Implementation of the 'nth' function which returns the 
 * nth element in an index enumerable.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class NthFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4195266884625834829L;

	public static final String NAME = "nth";
	
	protected Signature signature;
	
	public NthFunctionElement() {
		setFClass(FunctionClass.fcDerived);
		signature = new Signature();
		signature.setDomain(
				ListBackgroundElement.LIST_BACKGROUND_NAME,
				NumberBackgroundElement.NUMBER_BACKGROUND_NAME);
		signature.setRange(ElementBackgroundElement.ELEMENT_BACKGROUND_NAME);
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
		return list.get(n);
	}

	public Signature getSignature() {
		return signature;
	}
	
	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 2) 
				&& (args.get(0) instanceof AbstractListElement)
				&& (args.get(1) instanceof NumberElement)
				&& (((NumberElement)args.get(1)).isNatural());
	}
}
