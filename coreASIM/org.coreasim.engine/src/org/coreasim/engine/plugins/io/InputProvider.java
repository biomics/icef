/*	
 * InputProvider.java 	1.0 	$Revision: 243 $
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.io;

/** 
 * Interface to an input provider of IOPlugin
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public interface InputProvider {

	/**
	 * Implemented by an input provider, this method is used by IO Plugin 
	 * to get values from the environment.
	 * 
	 * @param message message that is sent to the environment
	 * @return input value
	 */
	public String getValue(String message);
	
}
