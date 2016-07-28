package org.coreasim.compiler.plugins.collection.code.ucode;

import java.util.List;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles an add operation on collections
 * @author Spellmaker
 *
 */
public class AddToHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		List<ASTNode> children = node.getAbstractChildNodes();
		CodeFragment lhs = engine.compile(
				children.get(0), CodeType.R);
		CodeFragment rhs = engine.compile(
				children.get(1), CodeType.L);

		result.appendFragment(lhs);
		result.appendFragment(rhs);
		String modifcoll = engine.getPath().getEntryName(LibraryEntryType.STATIC, "ModifiableCollection", "CollectionPlugin");
		result.appendLine("@decl(@RuntimePkg@.Location, loc)=(@RuntimePkg@.Location)evalStack.pop();\n");
		result.appendLine("@decl(@RuntimePkg@.Element, el) = (@RuntimePkg@.Element) evalStack.pop();\n");
		result.appendLine("@decl(" + modifcoll + ", coll) = (" + modifcoll + ")@RuntimeProvider@.getStorage().getValue(@loc@);\n");
		result.appendLine("@decl(@RuntimePkg@.UpdateList, ul) = new @RuntimePkg@.UpdateList();\n");
		result.appendLine("@ul@.addAll(@coll@.computeAddUpdate(@loc@, @el@, this.getUpdateResponsible()));\n");
		result.appendLine("evalStack.push(@ul@);\n");
	}

}
