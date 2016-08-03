/*	
 * SelfAgent.java  	1.0
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
 
package org.coreasim.engine.interpreter;

import org.coreasim.engine.absstorage.Element;

/** 
 * The first agent that runs the initial rule.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class SelfAgent extends Element {
	
	private String name;
	private String externalName;
	
	public SelfAgent(){
		name = "self";
	}
	public String toString() {
		return name;
	}
	
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public String getExternalName() {
		
		return externalName;
	}
	
	/**
	 * @param externalName the externalName to set
	 */
	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}
}
