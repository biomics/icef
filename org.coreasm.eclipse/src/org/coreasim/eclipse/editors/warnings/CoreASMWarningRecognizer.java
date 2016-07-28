package org.coreasim.eclipse.editors.warnings;

import java.util.ArrayList;
import java.util.List;

import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.eclipse.editors.ASMEditor;
import org.coreasim.eclipse.editors.SlimEngine;
import org.coreasim.engine.CoreASMWarning;

/**
 * The <code>CoreASMWarningRecognizer</code> collects warnings from the CoreASM Engine
 * @author Michael Stegmaier
 *
 */
public class CoreASMWarningRecognizer implements IWarningRecognizer {
	private final ASMEditor parentEditor;
	
	public CoreASMWarningRecognizer(ASMEditor parentEditor) {
		this.parentEditor = parentEditor;
	}
	
	@Override
	public List<AbstractWarning> checkForWarnings(ASMDocument document) {
		SlimEngine slimEngine = (SlimEngine)parentEditor.getParser().getSlimEngine();
		List<AbstractWarning> warnings = new ArrayList<AbstractWarning>();
		for (CoreASMWarning warning : slimEngine.getWarnings())
			warnings.add(new CoreASMEclipseWarning(warning, document));
		return warnings;
	}

}
