package org.coreasim.compiler.plugins.kernelextensions;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.kernelextensions.code.ucode.CompilerExtendedFunctionRulePolicyTermHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.kernelextensions.KernelExtensionsPlugin;

/**
 * Provides extended rule call possibilities.
 * The current version does not completely implement all
 * operations of the interpreter plugin
 * @author Spellmaker
 *
 */
public class CompilerKernelExtensionsPlugin extends CompilerCodePlugin implements CompilerPlugin {
	private Plugin parent;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerKernelExtensionsPlugin(Plugin parent) {
		this.parent = parent;
	}
	
	@Override
	public String getName() {
		return KernelExtensionsPlugin.PLUGIN_NAME;
	}

	@Override
	public Plugin getInterpreterPlugin() {
		return parent;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		//TODO: Write missing code handlers for the kernel extensions plugin
		register(new CompilerExtendedFunctionRulePolicyTermHandler(), CodeType.R, null, "ExtendedFunctionRulePolicyTermNode", null);
		
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}

}
