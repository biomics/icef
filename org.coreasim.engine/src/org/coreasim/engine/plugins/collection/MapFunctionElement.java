/*	
 * MapFunctionElement.java  	1.0
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
 
package org.coreasim.engine.plugins.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;

/** 
 * Function element providing the 'map' function.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class MapFunctionElement extends CollectionFunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7133491239825083204L;

	/** suggested name for this function */
	public static final String NAME = "map";

	private Signature signature = new Signature("ELEMENT", "FUNCTION", "ELEMENT");

	public MapFunctionElement(ControlAPI capi) {
		super(capi);
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
		Collection<? extends Element> values = ((Enumerable)args.get(0)).enumerate();
		FunctionElement f = (FunctionElement)args.get(1);
		Collection<Element> resultValues = new ArrayList<Element>();
		for (Element e: values) 
			resultValues.add(f.getValue(ElementList.create(e)));
		return ((AbstractMapElement)args.get(0)).getNewInstance(resultValues);
	}

	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 2) 
				&& (args.get(0) != null && args.get(0) instanceof AbstractMapElement)
				&& (args.get(1) != null && args.get(1) instanceof FunctionElement);
	}
}
