package org.coreasim.compiler.plugins.kernel.code.bcode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.compiler.plugins.kernel.CompilerKernelPlugin;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the CoreASM node of the specification.
 * @author Spellmaker
 *
 */
public class KernelInitHandler implements CompilerCodeHandler {	
	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		engine.getLogger().debug(CompilerKernelPlugin.class, "extracting initialization rule name");

		// should have exactly one child node which is an id
		String name = node.getAbstractChildNodes().get(0).getToken();

		name = "Rules." + name;

		engine.getMainFile().setInitRule(name);
	}

}
