/*
 * AbstractBagElement.java 		1.0
 * 
 * Copyright (c) 2007 Roozbeh Farahbod
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
import java.util.Map;
import java.util.Map.Entry;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.plugins.number.NumberElement;

/**
 * The base class for all bag elements (collections).
 *   
 * @author Roozbeh Farahbod
 * @see CollectionPlugin
 */

public abstract class AbstractBagElement extends AbstractMapElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6593928927031005335L;

	/**
	 * Creates a new instance of this element loaded with the given
	 * collection of elements. 
	 * 
	 * @param collection a non-null collection of elements
	 */
	public abstract AbstractBagElement getNewInstance(Collection<? extends Element> collection);
	
	/**
	 * Creates a new instance of this element loaded with the given 
	 * map. The map should be of the form of {@link Element} to {@link NumberElement}
	 * in which the numbers are all natural numbers;
	 * otherwise, this method throws {@link IllegalArgumentException}. 
	 * 
	 * @throws IllegalArgumentException if the given map is not of the form Element to NumberElement.
	 */
	public AbstractBagElement getNewInstance(Map<? extends Element, ? extends Element> map) {
		Collection<Element> bag = new ArrayList<Element>();
		
		for (Entry<? extends Element, ? extends Element> e: map.entrySet()) 
			if (e.getValue() instanceof NumberElement
					&& ((NumberElement)e.getValue()).isNatural()) {
				int number = (int)((NumberElement)e.getValue()).getValue();
				for (int i=0; i < number; i++)
					bag.add(e.getKey());
			} else
				throw new IllegalArgumentException("Expecting map of Element to NumberElement with natural numbers.");
		
		return getNewInstance(bag);
	}
	
}
