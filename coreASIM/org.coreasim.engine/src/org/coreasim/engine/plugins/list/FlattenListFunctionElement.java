/*	
 * FlattenListFunctionElement.java  
 * 
 * Copyright (C) 2008 Roozbeh Farahbod
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
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.collection.AbstractListElement;

/** 
 * Flattens a {@link ListElement} containing other {@link ListElement}s.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class FlattenListFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6583016746128289366L;

	public static final String NAME = "flattenList";
	
	protected Signature signature = null;

	public FlattenListFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}
	
	public Signature getSignature() {
		if (signature == null) {
			signature = new Signature();
			signature.setDomain(ListBackgroundElement.LIST_BACKGROUND_NAME);
			signature.setRange(ListBackgroundElement.LIST_BACKGROUND_NAME);
		}
		return signature;
	}
	
	/**
	 * If args contains only one instance of {@link AbstractListElement}, 
	 * this method returns a flatten {@link ListElement}.
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!(args.size() == 1 && args.get(0) instanceof AbstractListElement))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		return new ListElement(flattenList(((AbstractListElement) args.get(0)).getList()));
	}
	
	/*
	 * Flattens a list of elements such that all the immediate list elements are expanded.
	 */
	protected List<? extends Element> flattenList(List<? extends Element> list) {
		ArrayList<Element> result = null;
		boolean compound = false;
		for (Element e: list) 
			if (e instanceof AbstractListElement) 
				compound = true;
		if (compound) {
			result = new ArrayList<Element>();
			for (Element e: list) 
				if (e instanceof AbstractListElement)
					result.addAll(flattenList(((AbstractListElement) e).getList()));
				else
					result.add(e);
			return result;
		} else
			return list;
	}
}
