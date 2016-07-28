package org.coreasim.compiler.plugins.kernel.code.rcode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the self expression
 * @author Spellmaker
 *
 */
public class KernelSelfHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		result.appendLine("if(@RuntimeProvider@.getSelf(Thread.currentThread()) == null)\n"
				+ "evalStack.push(@RuntimePkg@.Element.UNDEF);\n"
				+ "else\n" + "evalStack.push(@RuntimeProvider@.getSelf(Thread.currentThread()));\n");

	}

}
