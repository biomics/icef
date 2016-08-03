/*	
 * MapletElement.java 	1.0
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
 
package org.coreasim.engine.plugins.map;

import org.coreasim.engine.absstorage.Element;

/** 
 * A placeholder for maplets
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class MapletElement extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2673833047675702607L;
	protected final Element key;
	protected final Element value;
	
	protected MapletElement(Element key, Element value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object anElement) {
		if (super.equals(anElement))
			return true;
		else
			if (anElement instanceof MapletElement) {
				MapletElement other = (MapletElement)anElement;
				return this.key.equals(other.key) && this.value.equals(other.value);
			} else
				return false;
	}

	@Override
	public String denotation() {
		return key.denotation() + "->" + value.denotation();
	}

	@Override
	public String toString() {
		return key + "->" + value;
	}

	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
	}
	
}
