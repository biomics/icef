package org.coreasim.compiler.plugins.letpolicy;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.letpolicy.code.ucode.LetPolicyHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.letpolicy.LetPolicyPlugin;

/**
 * Provides the let rule.
 * The let rule allows for the introduction of local
 * variables to the rule body
 * @author Spellmaker
 *
 */
public class CompilerLetPolicyPlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerLetPolicyPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}
	
	@Override
	public String getName() {
		return LetPolicyPlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new LetPolicyHandler(), CodeType.U, "Policy", "LetPolicy", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
