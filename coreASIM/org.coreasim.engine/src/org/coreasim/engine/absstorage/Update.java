/*	
 * Update.java 	1.0
 * 
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
 *
 */
 
package org.coreasim.engine.absstorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	Defines an update instruction. It consists of a <i>location</i>, a <i>value</i>,
 *  and an <i>update action</i>.
 *   
 *  @author  Roozbeh Farahbod
 */
public class Update {
	
	/** Name of the regular update action */
	public static final String UPDATE_ACTION = "updateAction";
	
	/** Location, value, and action of an update instruction */
	public final Location loc;
	public final Element value;
	public final String action;
	
	/** contributing agents */
	public final Set<Element> agents;

	/** originating nodes */
	public final Set<ScannerInfo> sources;
	
	/** 
	 * Creates a new update instructions.
	 * 
	 * @param loc location of the update
	 * @param value new value
	 * @param action action to be performed
	 * @param agents the agents providing this update; it can be more than 
	 * one agent if this is an aggregation of other updates.
	 * @param sources the set of sources (in the specification) that together generated this update.
	 */
	public Update(Location loc, Element value, String action, Set<Element> agents, Set<ScannerInfo> sources) {
		if (loc == null || value == null || action == null)
			throw new NullPointerException("Cannot create an update instruction with a null location, value, or action.");
		this.loc = loc;
		this.value = value;
		this.action = action;
		if (agents == null)
			this.agents = newAgentSet();
		else
			this.agents = Collections.unmodifiableSet(agents);
		if (sources == null)
			this.sources = newSourceSet();
		else
			this.sources = Collections.unmodifiableSet(sources);
	}
	
	/** 
	 * Creates a new update instructions.
	 * 
	 * @param loc location of the update
	 * @param value new value
	 * @param action action to be performed
	 * @param agent the agent providing this update.
	 * @param source the location of the spec where this update is generated
	 */
	public Update(Location loc, Element value, String action, Element agent, ScannerInfo source) {
		if (loc == null || value == null || action == null)
			throw new NullPointerException("Cannot create an update instruction with a null location, value, or action.");
		this.loc = loc;
		this.value = value;
		this.action = action;
		if (agent == null)
			//throw new NullPointerException("Cannot create an update instruction with a null agent.");
			this.agents = newAgentSet();
		else
			this.agents = newAgentSet(agent);
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
		if (obj instanceof Update) {
			Update u = (Update)obj;
			result = this.value.equals(u.value) 
					&& this.action.equals(u.action)
					&& this.loc.equals(u.loc);
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
		return loc.hashCode() + value.hashCode() + action.hashCode(); 
	}
	
	/**
	 * String view of udpates. Two equal updates should have the same string. 
	 *  
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + loc.toString() + ", " + value.denotation() + ", " + action + ")"; 
	}
	/**
	 * Creates a new set of agents (elements in general) with the given elements.
	 * @param agents given to create the agent set
	 * @return the set of agents
	 */
	private HashSet<Element> newAgentSet(Element ... agents) {
		HashSet<Element> result = new HashSet<Element>();
		for (Element a: agents)
			result.add(a);
		return result;
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
