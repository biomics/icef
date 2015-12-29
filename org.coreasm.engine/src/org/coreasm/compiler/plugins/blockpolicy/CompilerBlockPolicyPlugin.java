package org.coreasm.compiler.plugins.blockpolicy;

import org.coreasm.compiler.exception.CompilerException;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.engine.plugins.blockrule.BlockRulePlugin;
import org.coreasm.compiler.CodeType;
import org.coreasm.compiler.CompilerEngine;
import org.coreasm.compiler.interfaces.CompilerCodePlugin;
import org.coreasm.compiler.interfaces.CompilerPlugin;
import org.coreasm.compiler.plugins.blockpolicy.code.ucode.BlockPolicyHandler;
import org.coreasm.compiler.plugins.blockrule.code.ucode.BlockRuleHandler;

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
