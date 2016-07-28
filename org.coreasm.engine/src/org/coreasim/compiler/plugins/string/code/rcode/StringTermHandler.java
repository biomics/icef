package org.coreasim.compiler.plugins.string.code.rcode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles string creation
 * @author Spellmaker
 *
 */
public class StringTermHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		String stringelement = engine.getPath().getEntryName(LibraryEntryType.STATIC, "StringElement", "StringPlugin");
		result.appendLine("evalStack.push(new " + stringelement + "(\""
						+ replaceEscapeSeq(node.getToken()) + "\"));\n");
	}
	
	private String replaceEscapeSeq(String o){		
		return o.replaceAll("\n", "\\\\n").
				replaceAll("\\\"", "\\\\\"");
	}

}
