/*	
 * MacroCallPolicyNode.java 	1.0 	
 * 
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 * Macro Call policy node.
 *   
 * @author  Eric Rothstein
 * 
 */
public class MacroCallPolicyNode extends ASTNode {

	private static final long serialVersionUID = 1L;

	public MacroCallPolicyNode(MacroCallPolicyNode node) {
		super(node);
	}

	public MacroCallPolicyNode(ScannerInfo info) {
		super(
				Kernel.PLUGIN_NAME,
				ASTNode.POLICY_CLASS,
				"MacroCallPolicy",
				null,
				info);
	}

	/**
	 * @return the policy name 
	 */
	public String getPolicyName() {
		return getFunctionPolicyElement().getToken();
	}
	
	/**
	 * @return the function policy element
	 */
	public ASTNode getFunctionPolicyElement() {
		return this.getFirst();
	}
}
