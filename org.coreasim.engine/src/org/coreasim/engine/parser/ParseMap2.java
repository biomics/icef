package org.coreasim.engine.parser;

import org.codehaus.jparsec.functors.Map2;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.plugin.Plugin;

/** 
 * Specialized version of {@link Map3} that gets a plug-in name as well. 
 *   
 * @author Roozbeh Farahbod
 * 
 */

public abstract class ParseMap2 implements Map2<Node, Node, Node> {

	public final String pluginName;
	
	public ParseMap2(String pluginName) {
		this.pluginName = pluginName;
	}

	public ParseMap2(Plugin plugin) {
		this.pluginName = plugin.getName();
	}
}

