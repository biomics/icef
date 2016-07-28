package org.coreasim.compiler.plugins.kernel.code.lcode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles ids as locations
 * @author Spellmaker
 *
 */
public class KernelIDCodeHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine) {
		result.appendLine("evalStack.push(new @RuntimePkg@.Location(\""
				+ node.getToken()
				+ "\", new java.util.ArrayList<@RuntimePkg@.Element>()));\n");
	}

}
