/*	
 * EdgeBackgroundElement.java 
 * 
 * Copyright (C) 2010 Roozbeh Farahbod
 *
 * Last modified by $Author$ on $Date$.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
package org.coreasim.network.plugins.graph;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

/**
 * Background element of Edges
 * 
 * @author Roozbeh Farahbod
 *
 */
public class EdgeBackgroundElement extends BackgroundElement {

	public static final String BACKGROUND_NAME = "EDGE";
	
	@Override
	public Element getNewValue() {
		return new EdgeElement(Element.UNDEF, Element.UNDEF);
	}

	@Override
	protected Element getValue(Element e) {
		return BooleanElement.valueOf(e instanceof EdgeElement);
	}

}
