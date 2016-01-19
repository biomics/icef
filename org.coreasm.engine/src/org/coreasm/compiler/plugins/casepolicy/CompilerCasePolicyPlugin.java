package org.coreasm.compiler.plugins.casepolicy;

import org.coreasm.compiler.exception.CompilerException;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.engine.plugins.casepolicy.CasePolicyPlugin;
import org.coreasm.compiler.CodeType;
import org.coreasm.compiler.CompilerEngine;
import org.coreasm.compiler.interfaces.CompilerCodePlugin;
import org.coreasm.compiler.interfaces.CompilerPlugin;
import org.coreasm.compiler.plugins.caserule.code.ucode.CaseRuleHandler;

/**
 * Provides the case rule.
 * The case policy works a bit like the switch-case construct in normal
 * programming languages.
 * @author Spellmaker
 *
 */
public class CompilerCasePolicyPlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;
	
	/**
	 * Initializes a new case rule plugin
	 * @param parent The interpreter version
	 */
	public CompilerCasePolicyPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public String getName() {
		return CasePolicyPlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new CaseRuleHandler(), CodeType.U, "Policy", "CasePolicy", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
