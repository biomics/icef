package org.coreasim.compiler.plugins.set.code.rcode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles set enumerations
 * @author Spellmaker
 *
 */
public class EnumerateHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		for(int i = node.getAbstractChildNodes().size() - 1; i >= 0; i--){
			result.appendFragment(engine.compile(node.getAbstractChildNodes().get(i), CodeType.R));
		}
		result.appendLine("@decl(java.util.List<@RuntimePkg@.Element>,slist)=new java.util.ArrayList<@RuntimePkg@.Element>();\n");
		for(int i = 0; i < node.getAbstractChildNodes().size(); i++){
			result.appendLine("@slist@.add((@RuntimePkg@.Element)evalStack.pop());\n");
		}
		String setelement = engine.getPath().getEntryName(LibraryEntryType.STATIC, "SetElement", "SetPlugin");
		result.appendLine("evalStack.push(new " + setelement + "(@slist@));\n");
	}

}
