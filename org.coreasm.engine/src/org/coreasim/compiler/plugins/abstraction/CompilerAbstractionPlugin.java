package org.coreasim.compiler.plugins.abstraction;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.abstraction.code.ucode.AbstractionAbstractHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.abstraction.AbstractionPlugin;

/**
 * Provides the abstract rule.
 * The abstract rule allows the user to leave out an actual implementation.
 * @author Markus Brenner
 *
 */
public class CompilerAbstractionPlugin extends CompilerCodePlugin implements CompilerPlugin {

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new instance
	 * @param parent The interpreter version
	 */
	public CompilerAbstractionPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}
	
	@Override
	public String getName() {
		return AbstractionPlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new AbstractionAbstractHandler(), CodeType.U, "Rule", "AbstractRule", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
