package org.coreasim.compiler.plugins.turboasm;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.turboasm.code.rcode.ReturnRuleHandler;
import org.coreasim.compiler.plugins.turboasm.code.ucode.EmptyHandler;
import org.coreasim.compiler.plugins.turboasm.code.ucode.IterateRuleHandler;
import org.coreasim.compiler.plugins.turboasm.code.ucode.LocalRuleHandler;
import org.coreasim.compiler.plugins.turboasm.code.ucode.ReturnResultHandler;
import org.coreasim.compiler.plugins.turboasm.code.ucode.SeqRuleHandler;
import org.coreasim.compiler.plugins.turboasm.code.ucode.WhileRuleHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.turboasm.TurboASMPlugin;

/**
 * Provides the TurboASM functionality.
 * @author Spellmaker
 *
 */
public class CompilerTurboASMPlugin extends CompilerCodePlugin implements CompilerPlugin {

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerTurboASMPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public String getName() {
		return TurboASMPlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new ReturnRuleHandler(), CodeType.R, "Expression", "ReturnRule", null);
		
		CompilerCodeHandler cch = new SeqRuleHandler();
		register(cch, CodeType.U, "Rule", "SeqRule", null);
		register(cch, CodeType.U, "Rule", "SeqRuleBlock", null);
		register(new IterateRuleHandler(), CodeType.U, "Rule", "IterateRule", null);
		register(new WhileRuleHandler(), CodeType.U, "Rule", "WhileRule", null);
		register(new LocalRuleHandler(), CodeType.U, "Rule", "LocalRule", null);
		register(new ReturnResultHandler(), CodeType.U, "Rule", "ReturnResultRule", null);
		register(new EmptyHandler(), CodeType.U, "Rule", "EmptyRule", null);
	}
}
