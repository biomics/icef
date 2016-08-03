/*	
 * ReverseFunctionElement.java  	1.0
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
import org.coreasim.engine.absstorage.AbstractStorage;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.plugins.collection.AbstractListElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * Impelements the 'reverse' function.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class ReverseFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6778887027026443337L;
	public static final String NAME = "reverse";
	protected final ControlAPI capi;
	protected final AbstractStorage storage;
	
	public ReverseFunctionElement(ControlAPI capi) {
		setFClass(FunctionClass.fcDerived);
		this.capi = capi;
		this.storage = capi.getStorage();
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		AbstractListElement list = (AbstractListElement)args.get(0);
		ArrayList<Element> resultValues = new ArrayList<Element>();
		
		int i = list.size();
		while (i > 0) {
			resultValues.add(list.get(NumberElement.getInstance(i)));
			i--;
		}

		return new ListElement(resultValues);
	}

	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 1) 
				&& (args.get(0) instanceof AbstractListElement);
	}

}
