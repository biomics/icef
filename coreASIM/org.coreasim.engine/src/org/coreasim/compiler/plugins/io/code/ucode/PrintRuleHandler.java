package org.coreasim.compiler.plugins.io.code.ucode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the print rule
 * @author Spellmaker
 *
 */
public class PrintRuleHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		String stringelement = engine.getPath().getEntryName(LibraryEntryType.STATIC, "StringElement", "StringPlugin");
		result.appendFragment(engine.compile(node.getAbstractChildNodes().get(0), CodeType.R));
		result.appendLine("@decl(String,msg)=evalStack.pop().toString();\n");
		result.appendLine("@decl(@RuntimePkg@.UpdateList,ulist)=new @RuntimePkg@.UpdateList();\n");
		result.appendLine("@ulist@.add(new @RuntimePkg@.Update(new @RuntimePkg@.Location(\"output\", new java.util.ArrayList<@RuntimePkg@.Element>()), new " + stringelement + "(@msg@), \"printAction\", this.getUpdateResponsible(), null));\n");
		result.appendLine("evalStack.push(@ulist@);\n");
	}

}
