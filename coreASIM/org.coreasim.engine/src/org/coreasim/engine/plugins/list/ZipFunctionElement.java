/*
 * ZipFunctionElement.java	1.0
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
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.collection.AbstractListElement;

/** 
 * Function element providing the 'zip' function.
 *   
 * @author Marcel Dausend, Michael Stegmaier
 * 
 */
public class ZipFunctionElement extends ListFunctionElement {

	/** suggested name for this function */
	public static final String NAME = "zip";

	protected Signature signature;

	public ZipFunctionElement(ControlAPI capi) {
		super(capi);
		signature = new Signature();
		signature.setDomain(
				ListBackgroundElement.LIST_BACKGROUND_NAME,
				ListBackgroundElement.LIST_BACKGROUND_NAME);
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
		List<? extends Element> list0 = ((AbstractListElement) args.get(0)).getList();
		List<? extends Element> list1 = ((AbstractListElement) args.get(1)).getList();
		return new ListElement(zip(list0, list1));
	}

	/**
	 * Implementation of a Haskell like zip function for two lists.
	 * 
	 * @param list0
	 * @param list1
	 * @return list of tuples
	 */
	private List<? extends Element> zip(List<? extends Element> list0, List<? extends Element> list1) {
		ArrayList<Element> result = new ArrayList<Element>();
		for (int i = 0; i < Math.min(list0.size(), list1.size()); i++) {
			ArrayList<Element> tuple = new ArrayList<Element>();
			tuple.add(list0.get(i));
			tuple.add(list1.get(i));
			result.add(new ListElement(tuple));
		}
		return result;
	}

	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 2) 
				&& (args.get(0) != null && args.get(0) instanceof AbstractListElement)
				&& (args.get(1) != null && args.get(1) instanceof AbstractListElement);
	}
}
