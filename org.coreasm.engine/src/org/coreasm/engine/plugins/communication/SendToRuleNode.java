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
 * A node for Print Rule nodes.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class SendToRuleNode extends ASTNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SendToRuleNode(ScannerInfo info) {
		super(
				CommunicationPlugin.PLUGIN_NAME,
				ASTNode.RULE_CLASS,
				"SendToRule",
				null,
				info);
	}
	
	public SendToRuleNode(SendToRuleNode node) {
		super(node);
	}

	/**
	 * @return the message part of this node
	 */
	public ASTNode getMessage() {
		return this.getFirst();
	}

	public ASTNode getAddress() {
		return this.getMessage().getNext();
	}
}
