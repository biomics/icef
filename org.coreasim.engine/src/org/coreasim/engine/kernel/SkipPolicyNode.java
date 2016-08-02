/*	
 * SkipPolicyNode.java  	1.0
 *  
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 * This is the node type representing the 'skip' node. 
 * Other plugins can use this node to create a skip policy node.
 *   
 * @author  Eric Rothstein
 * 
 */
public class SkipPolicyNode extends ASTNode {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a 'none' policy node with the 
	 * given scanner information.
	 * 
	 * @param info scanner information
	 */
	public SkipPolicyNode(ScannerInfo info) {
		super(
				Kernel.PLUGIN_NAME,
				ASTNode.POLICY_CLASS,
				Kernel.GR_SKIP,
				Kernel.KW_SKIP,
				info,
				Node.KEYWORD_NODE
				);
	}
	
	/**
	 * @see ASTNode#ASTNode(ASTNode)
	 */
	public SkipPolicyNode(SkipPolicyNode node) {
		super(node);
	}
	
}
