/*	
 * BagUpdateElement.java  	1.0
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
 
package org.coreasim.engine.plugins.bag;

import org.coreasim.engine.absstorage.Element;

/** 
 * Basic update values for bags. A bag update value can be
 * either an addition of a value, a removal of a value, or an
 * absolute bag value.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class BagUpdateElement extends BagAbstractUpdateElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1972958637912659343L;

	public enum BagUpdateType {ADD, REMOVE};
	
	public final BagUpdateType type;
	public final Element value;
	
	/**
	 * Creates a new bag update element. 
	 * 
	 * @param type type of the update
	 * @param value value 
	 */
	public BagUpdateElement(BagUpdateType type, Element value) {
		if (type == null || value == null)
			throw new NullPointerException("Cannot create a bag update with null values.");
		this.type = type;
		this.value = value;
	}
	
	public boolean equals(Object o) {
		if (o instanceof BagUpdateElement) {
			BagUpdateElement theOther = (BagUpdateElement)o;
			return (this.type.equals(theOther.type) && this.value.equals(theOther.value));
		} else
			return false;
	}
	
	public int hashCode() {
		return value.hashCode();
	}
	
	public String toString() {
		return type.toString() + ":" + value.toString();
	}
}
