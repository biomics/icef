package org.coreasim.compiler.plugins.list.code.rcode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the list term.
 * Constructs lists
 * @author Spellmaker
 *
 */
public class ListTermHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {		
		result.appendLine("@decl(java.util.List<@RuntimePkg@.Element>,list)=new java.util.ArrayList<@RuntimePkg@.Element>();\n");
		for(ASTNode child : node.getAbstractChildNodes()){
			result.appendFragment(engine.compile(child, CodeType.R));
			result.appendLine("@list@.add((@RuntimePkg@.Element)evalStack.pop());\n");
		}
		String listelement = engine.getPath().getEntryName(LibraryEntryType.STATIC, "ListElement", "ListPlugin");
		result.appendLine("evalStack.push(new " + listelement + "(@list@));\n");
	}

}
