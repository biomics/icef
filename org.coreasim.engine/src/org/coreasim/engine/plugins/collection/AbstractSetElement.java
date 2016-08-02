/*
 * AbstractSetElement.java 		1.0
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

/**
 * The base class for all set elements.
 *   
 * @author Roozbeh Farahbod
 * @see CollectionPlugin
 */

public abstract class AbstractSetElement extends AbstractBagElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4252967322649067460L;

	/**
	 * Creates a new instance of this element loaded with the given
	 * set of elements.
	 */
	public abstract AbstractSetElement getNewInstance(Collection<? extends Element> set);
	
	/**
	 * Creates a new instance of this element loaded with the given 
	 * map. The map should be of the form of {@link Element} to {@link BooleanElement}.
	 * Otherwise, this method throws {@link IllegalArgumentException}. 
	 * 
	 * @throws IllegalArgumentException if the given map is not of the form Element to BooleanElement.
	 */
	public AbstractSetElement getNewInstance(Map<? extends Element, ? extends Element> map) {
		Set<Element> set = new HashSet<Element>();
		
		for (Entry<? extends Element, ? extends Element> e: map.entrySet()) 
			if (e.getValue() instanceof BooleanElement) {
				if (((BooleanElement)e.getValue()).getValue())
					set.add(e.getKey());
			} else
				throw new IllegalArgumentException("Expecting map of Element to BooleanElement.");
		
		return getNewInstance(set);
	}
	
	/**
	 * Returns the contents of this set in a java {@link Set}
	 * instance. If this is not possible, this method
	 * should throw an instance of
	 *  {@link UnsupportedOperationException}.
	 *  
	 *  @throws UnsupportedOperationException
	 */
	public abstract Set<? extends Element> getSet();

}
