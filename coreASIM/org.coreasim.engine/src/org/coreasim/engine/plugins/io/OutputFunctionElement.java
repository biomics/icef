/*	
 * OutputFunctionElement.java 	1.0 	
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.io;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.plugins.string.StringElement;

/** 
 * This class implements the 'output' function provided by the IO Plugin.
 *   
 * @author  Roozbeh Farahbod
 * 
 * @see org.coreasm.engine.plugins.io.IOPlugin
 */
public class OutputFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7050130799515405462L;
	private Set<Location> locations;
	private StringElement outputValues;
	
	public OutputFunctionElement() {
		setFClass(FunctionClass.fcOut);
		outputValues = new StringElement("");
		locations = new HashSet<Location>();
		locations.add(IOPlugin.PRINT_OUTPUT_FUNC_LOC);
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 0)
			return outputValues;
		else
			return Element.UNDEF;
	}

	/**
	 * Sets the value of this function only if there 
	 * is no argument.
	 */
	public void setValue(List<? extends Element> args, Element value) {
		if (args.size() == 0) {
			if (value instanceof StringElement) 
				outputValues = (StringElement)value;
			else
				outputValues = new StringElement(value.toString());
		}
	}

	/**
	 * Parameter <code>name</code> is ignored.
	 * 
	 * @see FunctionElement#getLocations(String)
	 */
	public Set<Location> getLocations(String name) {
		return locations;
	}

}
