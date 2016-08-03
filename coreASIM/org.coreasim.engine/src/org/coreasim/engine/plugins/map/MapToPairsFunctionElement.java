/*	
 * MapToPairsFunctionElement.java  	1.0
 * 
 * Copyright (C) 2009 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.map;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.list.ListElement;
import org.coreasim.engine.plugins.set.SetBackgroundElement;
import org.coreasim.engine.plugins.set.SetElement;

import java.util.Set;

/** 
 * A function that creates a collection of pairs from map elements.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class MapToPairsFunctionElement extends FunctionElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 525243095269796457L;

	public static final String NAME = "mapToPairs";
	
	protected Signature signature = null;
	
	public MapToPairsFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		final MapElement m = (MapElement)args.get(0);
		final Set<Element> set = new HashSet<Element>();
		for (Entry<Element, Element> e: m.getMap().entrySet())
			set.add(new ListElement(e.getKey(), e.getValue()));
		return new SetElement(set);
	}

	@Override
	public Signature getSignature() {
		if (signature == null) {
			signature = new Signature();
			signature.setDomain(MapBackgroundElement.NAME);
			signature.setRange(SetBackgroundElement.SET_BACKGROUND_NAME);
		}
		return signature;
	}
	
	/*
	 * Checks the arguments of the function
	 */
	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 1) && (args.get(0) instanceof MapElement);
	}

}
