/*	
 * DestroyAgentRuleNode.java 	1.0
 * 
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.communication;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 * A node for Delete Agent Rule nodes.
 *   
 * @author  Eric Rothstein
 * 
 */
public class DestroyAgentRuleNode extends ASTNode {




	public DestroyAgentRuleNode(ScannerInfo info) {
		super(
				CommunicationPlugin.PLUGIN_NAME,
				ASTNode.RULE_CLASS,
				"DestroyAgentRule",
				null,
				info);
	}
	
	public DestroyAgentRuleNode(DestroyAgentRuleNode node) {
		super(node);
	}

	/**
	 * @return the message part of this node
	 */
	public ASTNode getAgentName() {
		return (ASTNode) this.getChildNode("id");
	}
}
