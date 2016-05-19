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
public class SelfAgent extends Element {

	private static SelfAgent instance = new SelfAgent();
	private String name;
	private String externalName;
	
	private SelfAgent(){
		name = "self";
	}
	public String toString() {
		return name;
	}
	/**
	 * @return the instance
	 */
	public static SelfAgent getInstance() {
		return instance;
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
