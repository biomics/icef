/*	
 * FunctionDomainFunctionElement.java  	$Revision: 243 $
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
 
package org.coreasim.engine.plugins.signature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.plugins.list.ListElement;
import org.coreasim.engine.plugins.set.SetElement;

/** 
 * Provides a 'domain(f)' function that returns a set of lists of elements
 * for which function <i>f</i> has a value.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class FunctionDomainFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1159159777741408699L;
	/**
	 * Suggested name of this function
	 */
	public static final String FUNCTION_NAME = "domain";
	
	
	public FunctionDomainFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}
	
	/**
	 * If the args is a list of only one function element, this method
	 * returns a set of {@link ListElement ListElements} that contains values
	 * for which the given function element has values. Otherwise, returns
	 * {@link Element#UNDEF undef}.
	 * 
	 * @see org.coreasim.engine.absstorage.FunctionElement#getValue(java.util.List)
	 * @see ListElement
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 1) {
			Element e = args.get(0);
			if (e instanceof FunctionElement) {
				Set<Location> locs = ((FunctionElement)e).getLocations("");
				Set<Element> newSet = new HashSet<Element>();
				
				boolean unary = true;
				for (Location loc: locs) {
					newSet.add(new ListElement(loc.args));
					if (loc.args.size() != 1)
						unary = false;
				}
				
				// if the function is a unary function, 
				// return a set of values instead of list values
				if (unary) {
					Set<Element> newSet2 = new HashSet<Element>();
					for (Element l: newSet) 
						newSet2.add(((ListElement)l).get(1));
					newSet = newSet2;
				}
				
				return new SetElement(newSet);
				// Here it would be better if we used the SetBackground
				// to get a new value, but it doesn't matter for sets.
			}
		} 
		return Element.UNDEF;
	}

}
