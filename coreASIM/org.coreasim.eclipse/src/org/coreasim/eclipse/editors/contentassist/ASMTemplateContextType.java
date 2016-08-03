package org.coreasim.eclipse.editors.contentassist;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;

/**
 * @author Tobias
 * 
 * The context type "coreasim" describes what templates to grab when opening 
 * the default template.xml files (e.g coreasm-templtes.xml)
 */
public class ASMTemplateContextType extends org.eclipse.jface.text.templates.TemplateContextType {

	public static final String CONTEXT_TYPE = "coreasim";
	  
	public ASMTemplateContextType() {		
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
	}
}
