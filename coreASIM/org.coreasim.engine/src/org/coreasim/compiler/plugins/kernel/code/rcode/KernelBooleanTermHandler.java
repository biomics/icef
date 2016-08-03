package org.coreasim.compiler.plugins.kernel.code.rcode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles boolean creation
 * @author Spellmaker
 *
 */
public class KernelBooleanTermHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		result.appendLine("evalStack.push("
						+ ((node.getToken().equals("true")) ? "@RuntimePkg@.BooleanElement.TRUE"
								: "@RuntimePkg@.BooleanElement.FALSE")
						+ ");");
	}

}
