package org.coreasim.compiler.plugins.extendrule;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.extendrule.code.ucode.ExtendRuleHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.extendrule.ExtendRulePlugin;

/**
 * Provides the extend rule.
 * Introduces a new element into an universe
 * and binds it to a local variable in its body
 * @author Spellmaker
 *
 */
public class CompilerExtendRulePlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerExtendRulePlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public String getName() {
		return ExtendRulePlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new ExtendRuleHandler(), CodeType.U, "Rule", "ExtendRule", null);
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
}
