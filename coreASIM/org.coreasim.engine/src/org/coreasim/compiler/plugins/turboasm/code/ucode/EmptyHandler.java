package org.coreasim.compiler.plugins.turboasm.code.ucode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the empty rule
 * @author Spellmaker
 *
 */
public class EmptyHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		result.appendLine("evalStack.push(new @RuntimePkg@.UpdateList());\n");
	}

}
