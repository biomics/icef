package org.coreasim.eclipse.editors;

import org.coreasim.engine.interpreter.ASTNode;
import org.eclipse.jface.text.ITextSelection;

public interface IASMSelectionListener {
	public void selectionChanged(ASMEditor editor, ITextSelection selection, ASTNode root);
}
