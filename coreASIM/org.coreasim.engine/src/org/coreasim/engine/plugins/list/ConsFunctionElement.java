/*	
 * ConsFunctionsElement.java  	
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

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementBackgroundElement;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.collection.AbstractListElement;

/** 
 * Implementation of the 'cons' function for lists.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class ConsFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7382940992293145227L;
	public static final String NAME = "cons";

	public ConsFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}
	
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		List<Element> newData = new ArrayList<Element>(((AbstractListElement)args.get(1)).getList());
		newData.add(0, args.get(0));
		return new ListElement(newData);
	}
	
	public Signature getSignature() {
		Signature sig = new Signature();

		// TODO the domain should be ABSTRACT_LIST or something like that
		sig.setDomain(ElementBackgroundElement.ELEMENT_BACKGROUND_NAME, 
				ListBackgroundElement.LIST_BACKGROUND_NAME);
		sig.setRange(ListBackgroundElement.LIST_BACKGROUND_NAME);
		
		return sig;
	}
	
	/*
	 * Checks the arguments of the function
	 */
	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 2) && (args.get(1) instanceof AbstractListElement);
	}
}
