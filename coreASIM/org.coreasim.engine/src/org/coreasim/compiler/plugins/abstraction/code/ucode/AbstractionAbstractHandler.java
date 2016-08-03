package org.coreasim.compiler.plugins.abstraction.code.ucode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the abstract rule
 * @author Spellmaker
 *
 */
public class AbstractionAbstractHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		
		String iopluginloc = engine.getPath().getEntryName(LibraryEntryType.STATIC, "IOPlugin", "IOPlugin");
		String stringelement = engine.getPath().getEntryName(LibraryEntryType.STATIC, "StringElement", "StringPlugin");
		
		result.appendLine("");
		result.appendFragment(engine.compile(node.getAbstractChildNodes().get(0), CodeType.R));
		result.appendLine("@decl(String,msg)=evalStack.pop().toString();\n");
		result.appendLine("@decl(@RuntimePkg@.UpdateList,ulist)=new @RuntimePkg@.UpdateList();\n");
		result.appendLine("@ulist@.add(new @RuntimePkg@.Update(" + iopluginloc + ".PRINT_OUTPUT_FUNC_LOC , new " + stringelement + "(\"Abstract Call: \" + @msg@), " + iopluginloc + ".PRINT_ACTION, this.getUpdateResponsible(), null));\n");
		result.appendLine("evalStack.push(@ulist@);\n");
	}

}
