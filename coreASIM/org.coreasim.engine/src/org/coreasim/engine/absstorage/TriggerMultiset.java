/*
 * TriggerMultiset		1.0
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */

 
package org.coreasim.engine.absstorage;

import java.util.Collection;

import org.coreasim.util.HashMultiset;

/** 
 * Provides a multiset of updates. This class extends the <code>HashMultiset</code> class
 * and specializes it to a multiset of updates. 
 *   
 *  @author  Eric Rothstein
 *  
 */
public class TriggerMultiset extends HashMultiset<Trigger> {

	/**
	 * Creates a new empty update multiset.
	 * 
	 * @see HashMultiset#HashMultiset()
	 */
	public TriggerMultiset() {
		super();
	}

	/**
	 * Creates a new update multiset with the given updates.
	 * 
	 * @see HashMultiset#HashMultiset(Collection)
	 */
	public TriggerMultiset(Collection<? extends Trigger> c) {
		super(c);
	}

	/**
	 * Creates a new update multiset with the given updates.
	 * 
	 * @see HashMultiset#HashMultiset(Object[])
	 */
	public TriggerMultiset(Trigger... elements) {
		super(elements);
	}

}
