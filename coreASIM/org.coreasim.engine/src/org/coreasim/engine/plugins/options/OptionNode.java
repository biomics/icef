/*	
 * OptionNode.java  	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 * This file contains source code contributed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS) 
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.options;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 * This node holds Option name-value pairs.
 *   
 * @see OptionsPlugin
 * 
 * @author  Roozbeh Farahbod
 * 
 */
public class OptionNode extends ASTNode {

	private static final long serialVersionUID = 1L;

	public OptionNode(ScannerInfo info) {
		super(OptionsPlugin.PLUGIN_NAME,
				ASTNode.DECLARATION_CLASS,
				"PropertyOption",
				null,
				info);
	}
	
	public OptionNode(OptionNode node) {
		super(node);
	}
	
	/**
	 * @return the name of this property/option
	 */
	public String getOptionName() {
		return getFirst().getToken();
	}

	/**
	 * @return the value of this property/option
	 */
	public String getOptionValue() {
		final ASTNode node = getFirst().getNext();
		//FIXME What do we want this node to be? a String? A term?
		if (node != null) {
			if (node.getToken() != null)
				return node.getToken();
			else
				if (node.getGrammarClass().equals(ASTNode.FUNCTION_RULE_POLICY_CLASS))
					return node.getFirstASTNode().getToken();
				else
					return "";
		}
		else 
			return "";
	}
}
