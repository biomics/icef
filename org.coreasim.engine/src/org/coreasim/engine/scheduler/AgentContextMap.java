/*
 * AgentContextMap.java 		1.0
 * 
 * Copyright (c) 2009 Roozbeh Farahbod
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
package org.coreasim.engine.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.coreasim.engine.absstorage.Element;

/**
 * Keeps a volatile map of agent context
 *
 */
public class AgentContextMap {

	private final Map<Element, AgentContext> map = new HashMap<Element, AgentContext>();
	
	public synchronized void put(Element agent, AgentContext context) {
		map.put(agent, context);
	}
	
	public synchronized AgentContext get(Element agent) {
		return map.get(agent);
	}
	
	public synchronized void clear() {
		for (AgentContext context : map.values()) {
			context.interpreter.dispose();
			context.interpreter = null;
			context.nodeCopyCache.clear();
		}
		map.clear();
	}
}