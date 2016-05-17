/*	
 * InitAgent.java  	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.interpreter;

import org.coreasm.engine.absstorage.Element;

/** 
 * The first agent that runs the initial rule.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class EnvironmentAgent extends Element {

	private static EnvironmentAgent instance = new EnvironmentAgent();
	private String name;
	
	private EnvironmentAgent(){
		name = "self";
	}
	public String toString() {
		return name;
	}
	/**
	 * @return the instance
	 */
	public static EnvironmentAgent getInstance() {
		return instance;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
