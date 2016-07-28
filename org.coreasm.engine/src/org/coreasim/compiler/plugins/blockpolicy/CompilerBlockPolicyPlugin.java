package org.coreasim.compiler.plugins.blockpolicy;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.blockpolicy.code.ucode.BlockPolicyHandler;
import org.coreasim.compiler.plugins.blockrule.code.ucode.BlockRuleHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.blockrule.BlockRulePlugin;

/**
 * Provides the block rule.
 * Block rules allow for parallel execution (in terms of abstract state machines)
 * of rules
 * @author Spellmaker
 *
 */
public class CompilerBlockPolicyPlugin extends CompilerCodePlugin implements CompilerPlugin{

	private Plugin interpreterPlugin;

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}

	/**
	 * Constructs a new instance
	 * @param parent The interpreter version
	 */
	public CompilerBlockPolicyPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public String getName() {
		return BlockRulePlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new BlockPolicyHandler(), CodeType.U, null, "BlockPolicy", null);
	}
}
