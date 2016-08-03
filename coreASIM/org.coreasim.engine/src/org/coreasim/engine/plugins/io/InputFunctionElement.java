/*	
 * InputFunctionElement.java 	1.0 	
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
 */
 
package org.coreasim.engine.plugins.io;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.plugins.string.StringElement;

/** 
 * Implements the <i>input</i> monitored function provided by IO Plugin.
 *   
 * @author  Roozbeh Farahbod
 * 
 * @see org.coreasim.engine.plugins.io.IOPlugin
 */
public class InputFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4733719571260543062L;
	private final IOPlugin plugin;
	
	/**
	 * Creates a new input function element with the given
	 * link to an IOPlugin.
	 *  
	 * @param ioPlugin the IOPlugin that created this object
	 * @see IOPlugin
	 */
	public InputFunctionElement(IOPlugin ioPlugin) {
		this.plugin = ioPlugin;
		this.setFClass(FunctionClass.fcMonitored);
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		String msg;
		// get the message argument
		if (args.size() == 0)
			msg = "";
		else
			msg = args.get(0).toString();
		
		if (plugin.inputProvider != null) {
			String input = plugin.inputProvider.getValue(msg);
			if (input == null)
				return Element.UNDEF;
			else
				return new StringElement(input);
		} else
			return Element.UNDEF;
	}
}
