/*	
 * MapBackgroundElement.java  	1.0
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

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

/** 
 * Background of MapElements.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class MapBackgroundElement extends BackgroundElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6332067996609962004L;

	public static final String NAME = "MAP";
	
	protected static final MapElement NEW_INSTANCE = new MapElement();
	
	/* 
	 * @see org.coreasm.engine.absstorage.BackgroundElement#getNewValue()
	 */
	@Override
	public Element getNewValue() {
		return NEW_INSTANCE;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.AbstractUniverse#getValue(org.coreasm.engine.absstorage.Element)
	 */
	@Override
	protected Element getValue(Element e) {
		if (e instanceof MapElement)
			return BooleanElement.TRUE;
		else
			return BooleanElement.FALSE;
	}

}
