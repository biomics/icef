package org.coreasim.compiler.plugins.choosepolicy;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.chooserule.code.ucode.ChooseRuleHandler;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.choosepolicy.ChoosePolicyPlugin;

/**
 * Provides the choose rule.
 * The choose rule allows to execute a rule with a randomly choosen
 * variable assignment.
 * The full form is:
 * choose x1 in l1, x2 in l2, ... with guard do R1 ifnone R2
 * If any of the lists l1...ln is empty or the guard cannot be fulfilled
 * with any assignment for x1...xn, the ifnone rule is executed with
 * x1 = x2...=xn = undef
 * 
 * This plugin also provides the pick expression, which picks a random value
 * out of an {@link Enumerable}
 * @author Spellmaker
 *
 */
public class CompilerChoosePolicyPlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;
	
	/**
	 * Initializes a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerChoosePolicyPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}
	
	@Override
	public String getName() {
		return ChoosePolicyPlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new ChooseRuleHandler(), CodeType.U, "Policy", "ChoosePolicy", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
