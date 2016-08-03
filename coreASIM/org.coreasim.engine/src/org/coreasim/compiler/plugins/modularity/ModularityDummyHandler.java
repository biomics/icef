package org.coreasim.compiler.plugins.modularity;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles empty modularity nodes.
 * Does not provide functionality, only catches empty nodes
 * @author Spellmaker
 *
 */
public class ModularityDummyHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		//do nothing, this handler only exists to stop empty modularity nodes
		//from harming the compiler
	}

}
