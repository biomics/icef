package org.coreasim.compiler.plugins.conditionalrule;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.conditionalrule.code.rcode.ConditionalTermHandler;
import org.coreasim.compiler.plugins.conditionalrule.code.ucode.ConditionalRuleHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.conditionalrule.ConditionalRulePlugin;

/**
 * Provides the conditional rule.
 * Allows for the use of if guard then r1 else r2 rules.
 * Also provides an expression similar to the ternary ? operator
 * @author Spellmaker
 *
 */
public class CompilerConditionalRulePlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerConditionalRulePlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public String getName() {
		return ConditionalRulePlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new ConditionalRuleHandler(), CodeType.U, "Rule", "ConditionalRule", null);
		register(new ConditionalTermHandler(), CodeType.R, "Expression", "ConditionalTerm", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
