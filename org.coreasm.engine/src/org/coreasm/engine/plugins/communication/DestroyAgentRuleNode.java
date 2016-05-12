/*	
 * PrintRuelNode.java 	1.5 	$Revision: 243 $
 * 
 * Copyright (C) 2006-2007 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.plugins.communication;

import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.ScannerInfo;

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
