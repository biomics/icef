package org.coreasim.compiler.plugins.number.code.rcode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the creation of number ranges
 * @author Spellmaker
 *
 */
public class NumberRangeHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		String numberelement = engine.getPath().getEntryName(LibraryEntryType.STATIC, "NumberElement", "NumberPlugin");
		String numberrange = engine.getPath().getEntryName(LibraryEntryType.STATIC, "NumberRangeElement", "NumberPlugin");
		
		CodeFragment start = engine.compile(
				node.getAbstractChildNodes().get(0), CodeType.R);
		CodeFragment end = engine.compile(
				node.getAbstractChildNodes().get(1), CodeType.R);
		CodeFragment step = null;
		if (node.getAbstractChildNodes().size() == 3) {
			step = engine.compile(
					node.getAbstractChildNodes().get(2), CodeType.R);
		}

		result.appendFragment(start);
		result.appendFragment(end);
		if (step != null) {
			result.appendFragment(step);
			result.appendLine("@decl(double,step)=(((" + numberelement + ")evalStack.pop()).getValue());\n");
		}
		result.appendLine("@decl(double,end)=(((" + numberelement + ")evalStack.pop()).getValue());\n");
		result.appendLine("@decl(double,start)=(((" + numberelement + ")evalStack.pop()).getValue());\n");
		if (step != null) {
			result.appendLine("evalStack.push(new " + numberrange + "(@start@,@end@,@step@));\n");
		} else {
			result.appendLine("evalStack.push(new " + numberrange + "(@start@,@end@));\n");
		}
	}

}
