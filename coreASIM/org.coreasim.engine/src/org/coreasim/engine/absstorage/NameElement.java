/*	
 * NameElement.java 	1.0 	
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
 *
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
 *
 */
 
package org.coreasim.engine.absstorage;

/** 
 * An element that has a name. 
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class NameElement extends Element {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 788779260042307559L;
	/** name of this element */
	public final String name;
	
	/**
	 * Creates a new element with the given name.
	 */
	public NameElement(String name) {
		this.name = name;
	}

	/**
	 * @return name of this element
	 */
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object anElement) {
		if (anElement instanceof NameElement) {
			return this.name.equals(((NameElement)anElement).name);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
}
