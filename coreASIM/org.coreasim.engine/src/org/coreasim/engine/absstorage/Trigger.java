/*
 * Trigger 		1.0
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.absstorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	Defines a trigger instruction. It consists of an <i>agent</i>
 *   
 *  @author  Eric Rothstein
 */
public class Trigger {
	
	/** Name of the regular update action */
	public static final String TRIGGER_ACTION = "triggerAction";
	

	public final Element agent;
	public final String action;

	/** originating nodes */
	public final Set<ScannerInfo> sources;
	
	/** 
	 * Creates a new trigger instructions.
	 * 
	 * @param agent the agent to be triggered
	 * @param value new value
	 * @param action action to be performed
	 * @param agents the agents providing this update; it can be more than 
	 * one agent if this is an aggregation of other updates.
	 * @param sources the set of sources (in the specification) that together generated this update.
	 */
	public Trigger(Element agent, String action, Set<ScannerInfo> sources) {
		if (agent == null || action == null)
			throw new NullPointerException("Cannot create a trigger instruction with a null agent or action");
		this.agent = agent;
		this.action = action;
		if (sources == null)
			this.sources = newSourceSet();
		else
			this.sources = Collections.unmodifiableSet(sources);
	}
	
	/** 
	 * Creates a new trigger instructions.
	 * 
	 * @param loc location of the update
	 * @param value new value
	 * @param action action to be performed
	 * @param agent the agent providing this update.
	 * @param source the location of the spec where this update is generated
	 */
	public Trigger(Element agent, String action, ScannerInfo source) {
		if (agent == null|| action == null)
			throw new NullPointerException("Cannot create an update instruction with a null agent or action.");
			//throw new NullPointerException("Cannot create an update instruction with a null agent.");
			this.agent = agent;
			this.action = action;
		if (source == null)
			this.sources = newSourceSet();
		else
			this.sources = newSourceSet(source);
	}
	
	/**
	 * Compares this object to the specified object. If
	 * the specified object is an <code>Update</code> with
	 * the same location, value, and action as of this object,
	 * returns <code>true</code>; otherwise returns <code>false</code>.
	 * 
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Trigger) {
			Trigger u = (Trigger)obj;
			result = this.agent.equals(u.agent)&& this.action.equals(u.action);
		}
		return result;
	}
	
	/**
	 * Hash code for updates. Must be overridden because equality is overridden. 
	 *  
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return agent.hashCode()+ action.hashCode(); 
	}
	
	/**
	 * String view of triggers. Two equal triggers should have the same string. 
	 *  
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + agent.toString()+ ", " + action + ")"; 
	}
	
	/**
	 * Puts all the sources in one source set
	 * @param sources
	 * @return the source set with all the sources
	 */
	private HashSet<ScannerInfo> newSourceSet(ScannerInfo ... sources) {
		HashSet<ScannerInfo> result = new HashSet<ScannerInfo>();
		for (ScannerInfo a: sources)
			result.add(a);
		return result;
	}
}
