/*	
 * CollectionFunctionElement.java  	1.0
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

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.absstorage.AbstractStorage;
import org.coreasim.engine.absstorage.FunctionElement;

/** 
 * The general class of derived functions provided by the 
 * collection plugin.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public abstract class CollectionFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6864413176275843095L;
	protected final ControlAPI capi;
	protected final AbstractStorage storage;
	
	public CollectionFunctionElement(ControlAPI capi) {
		setFClass(FunctionClass.fcDerived);
		this.capi = capi;
		this.storage = capi.getStorage();
	}
	
}
