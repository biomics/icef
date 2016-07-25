package org.coreasm.compiler.plugins.letpolicy;

import org.coreasm.compiler.exception.CompilerException;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.engine.plugins.letpolicy.LetPolicyPlugin;
import org.coreasm.compiler.CodeType;
import org.coreasm.compiler.CompilerEngine;
import org.coreasm.compiler.interfaces.CompilerCodePlugin;
import org.coreasm.compiler.interfaces.CompilerPlugin;
import org.coreasm.compiler.plugins.letpolicy.code.ucode.LetPolicyHandler;

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
