package org.coreasim.eclipse.editors.warnings;

import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.engine.interpreter.Node;

public class DanglingElseWarning extends AbstractWarning {

	public DanglingElseWarning(Node node, ASMDocument document) {
		super("Dangling else", "DanglingElse " + document.getNodePosition(node.getParent()), node, document);
	}
}
