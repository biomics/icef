package org.coreasim.compiler.plugins.number.code.rcode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the sizeof operation
 * @author Spellmaker
 *
 */
public class SizeOfHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		CodeFragment en = engine.compile(
				node.getAbstractChildNodes().get(0), CodeType.R);
		result.appendFragment(en);
		result.appendLine("@decl(java.util.List<@RuntimePkg@.Element>,list)=new java.util.ArrayList<@RuntimePkg@.Element>();\n");
		result.appendLine("@list@.add((@RuntimePkg@.Element)evalStack.pop());\n");
		result.appendLine("evalStack.push(@RuntimeProvider@.getStorage().getValue(new @RuntimePkg@.Location(\"size\", @list@)));\n");
	}

}
