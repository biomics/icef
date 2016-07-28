package org.coreasim.compiler.plugins.letrule;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.letrule.code.ucode.LetRuleHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.letrule.LetRulePlugin;

/**
 * Provides the let rule.
 * The let rule allows for the introduction of local
 * variables to the rule body
 * @author Spellmaker
 *
 */
public class CompilerLetRulePlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerLetRulePlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}
	
	@Override
	public String getName() {
		return LetRulePlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new LetRuleHandler(), CodeType.U, "Rule", "LetRule", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
