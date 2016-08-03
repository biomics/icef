/*	
 * ParseMap.java 	$Revision: 243 $
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
 
package org.coreasim.engine.parser;

import org.codehaus.jparsec.functors.Map;
import org.coreasim.engine.plugin.Plugin;

/** 
 * Specialized version of {@link Map} that gets a plug-in name as well. 
 *   
 * @author Roozbeh Farahbod
 * 
 */

public abstract class ParseMap<To, From> implements Map<To, From> {

	public final String pluginName;
	
	public ParseMap(String pluginName) {
		this.pluginName = pluginName;
	}

	public ParseMap(Plugin plugin) {
		this.pluginName = plugin.getName();
	}
}

