package org.coreasim.eclipse.editors.warnings;

import java.util.List;

import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;

/**
 * This warning indicates an undefined identifier.
 * @author Michael Stegmaier
 *
 */
public class UndefinedIdentifierWarning extends AbstractWarning {

	public UndefinedIdentifierWarning(String identifier, List<ASTNode> arguments, Node node, ASMDocument document) {
		super("Undefined identifier encountered: " + identifier, "UndefinedIdentifier " + identifier + " " + arguments.size(), document.getNodePosition(node), identifier.length());
	}
}
