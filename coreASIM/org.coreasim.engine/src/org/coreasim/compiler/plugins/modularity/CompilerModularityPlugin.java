package org.coreasim.compiler.plugins.modularity;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.engine.plugin.Plugin;

/**
 * Provides modularity support.
 * Does not actually provide functionality, as the interpreter
 * version handles everything
 * @author Spellmaker
 *
 */
public class CompilerModularityPlugin extends CompilerCodePlugin implements
		CompilerPlugin {

	private Plugin parent;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerModularityPlugin(Plugin parent) {
		this.parent = parent;
	}
	
	@Override
	public String getName() {
		return parent.getName();
	}

	@Override
	public Plugin getInterpreterPlugin() {
		return parent;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		this.register(new ModularityDummyHandler(), null, null, null, null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}

}
