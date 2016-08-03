/*	
 * PolicyOrFuncElementNode.java 	1.0 	
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
 * Node of policy/function element terms.
 * 
 * @author Eric Rothstein
 * 
 */
public class PolicyOrFuncElementNode extends ASTNode {

	private static final long serialVersionUID = 1L;

	public PolicyOrFuncElementNode(PolicyOrFuncElementNode node) {
		super(node);
	}
	
	public PolicyOrFuncElementNode(ScannerInfo info) {
		super(
				null,
				ASTNode.EXPRESSION_CLASS,
				Kernel.GR_POLICY_OR_FUNCTION_ELEMENT_TERM,
				null,
				info
				);
	}

	/**
	 * @return the name of the policy or function
	 */
	public String getElementName() {
		return this.getFirst().getToken();
	}
	
		
}
