/*	
 * SendToRuleNode.java 	1.0
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
 * A node for Send Rule nodes.
 *   
 * @author  Eric Rothstein
 * 
 */
public class SendToRuleNode extends ASTNode {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
//	private static final long serialVersionUID = -5219646772050332413L;

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
	/**
	 * @return the destination address
	 */
	public ASTNode getAddress() {
		return this.getMessage().getNext();
	}
	
	/**
	 * @return the subject of the message
	 */
	public ASTNode getSubject() {
		return this.getAddress().getNext();
	}
}
