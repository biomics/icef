/*
 * CoreASMEngineFactory.java 	1.0 	$Revision: 243 $
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
 * 
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine;

/**
 * Factory class to create actual CoreASM engines. 
 * 
 * @author Roozbeh Farahbod
 * 
 */
public class CoreASIMEngineFactory {

	/**
	 * Creates and returns a new CoreASM engine.
	 */
	public static CoreASIMEngine createEngine() {
		return new Engine();
	}
	
	/**
	 * Creates and returns a new CoreASM engine with the 
	 * given properties.
	 */
	public static CoreASIMEngine createEngine(java.util.Properties properties) {
		return new Engine(properties);
	}
}
