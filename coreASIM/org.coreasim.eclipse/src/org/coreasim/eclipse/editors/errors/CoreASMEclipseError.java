package org.coreasim.eclipse.editors.errors;

import java.util.Map;

import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.engine.CoreASIMError;

/**
 * This class represents errors from CoreASM.
 * @author Michael Stegmaier
 *
 */
public class CoreASMEclipseError extends AbstractError {
	
	public CoreASMEclipseError(CoreASIMError error, ASMDocument document) {
		super(ErrorType.COREASM_ERROR);
		set(AbstractError.DESCRIPTION, "CoreASM Error: " + error.showError(null, null));
		set(AbstractError.POSITION, document.getCharPosition(error.getPos(), error.getSpec()));
		set(AbstractError.LENGTH, document.calculateLength(error.node));
	}
	
	protected CoreASMEclipseError(Map<String, String> attributes)
	{
		super(attributes);
	}
}
