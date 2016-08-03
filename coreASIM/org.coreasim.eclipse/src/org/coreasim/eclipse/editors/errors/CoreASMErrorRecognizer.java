package org.coreasim.eclipse.editors.errors;

import java.util.List;

import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.eclipse.editors.ASMEditor;
import org.coreasim.eclipse.editors.SlimEngine;
import org.coreasim.engine.CoreASIMError;

/**
 * The <code>CoreASMErrorRecognizer</code> collects errors from the CoreASM Engine
 * @author Michael Stegmaier
 *
 */
public class CoreASMErrorRecognizer implements ITextErrorRecognizer {
	private final ASMEditor parentEditor;
	
	public CoreASMErrorRecognizer(ASMEditor parentEditor) {
		this.parentEditor = parentEditor;
	}
	
	@Override
	public void checkForErrors(ASMDocument document, List<AbstractError> errors) {
		SlimEngine slimEngine = (SlimEngine)parentEditor.getParser().getSlimEngine();
		for (CoreASIMError error : slimEngine.getErrors())
			errors.add(new CoreASMEclipseError(error, document));
	}
}
