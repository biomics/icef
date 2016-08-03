package org.coreasim.compiler.plugins.casepolicy;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.caserule.code.ucode.CaseRuleHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.casepolicy.CasePolicyPlugin;

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
