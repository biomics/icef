package org.coreasim.eclipse.editors.warnings;

import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.engine.CoreASIMWarning;
import org.eclipse.jface.text.IDocument;

/**
 * This class represents warnings from CoreASM.
 * @author Michael Stegmaier
 *
 */
public class CoreASMEclipseWarning extends AbstractWarning {

	public CoreASMEclipseWarning(CoreASIMWarning warning, IDocument document) {
		super("CoreASM Warning: " + warning.showWarning(null, null), "CoreASMWarning", ((ASMDocument)document).getCharPosition(warning.getPos(), warning.getSpec()), ((ASMDocument)document).calculateLength(warning.node));
	}
}
