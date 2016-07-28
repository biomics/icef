/*	
 * EnqueueRuleNode.java 	$Revision: 243 $
 * 
 * Copyright (c) 2007 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.queue;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	AST node for 'enqueue' rules.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public class EnqueueRuleNode extends ASTNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnqueueRuleNode(ScannerInfo info) {
		super(
				QueuePlugin.PLUGIN_NAME,
				ASTNode.RULE_CLASS,
				"EnqueueRule",
				null,
				info);
	}
	
	/**
	 * @param node
	 */
	public EnqueueRuleNode(EnqueueRuleNode node) {
		super(node);
	}

	/**
	 * @return the node representing the location where the popped value should be stored
	 */
	public ASTNode getElementNode() {
		return getFirst();
	}
	
	/**
	 * @return the node representing the stack
	 */
	public ASTNode getQueueNode() {
		return getFirst().getNext();
	}
}
