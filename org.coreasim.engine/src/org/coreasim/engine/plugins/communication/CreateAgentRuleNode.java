/*	
 * CreateAgentNode.java 	1.0
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
 * A node for Create Rule nodes.
 *   
 * @author  Eric Rothstein
 * 
 */
public class CreateAgentRuleNode extends ASTNode {



	/**
	 * 
	 */
	private static final long serialVersionUID = -5219646772050332413L;

	public CreateAgentRuleNode(ScannerInfo info) {
		super(
				CommunicationPlugin.PLUGIN_NAME,
				ASTNode.RULE_CLASS,
				"CreateAgentRule",
				null,
				info);
	}
	
	public CreateAgentRuleNode(CreateAgentRuleNode node) {
		super(node);
	}

	/**
	 * @return the message part of this node
	 */
	public ASTNode getAgentName() {
		return (ASTNode) this.getChildNode("id");
	}
	/**
	 * @return the init rule part of the node
	 */
	public ASTNode getAgentInit() {
		return (ASTNode) this.getChildNode("init");
	}
	
	/**
	 * @return the program part of the node
	 */
	public ASTNode getAgentProgram() {
		return (ASTNode) this.getChildNode("program");
	}

	/**
	 * @return the policy part of the node
	 */
	public ASTNode getAgentPolicy() {
		return (ASTNode) this.getChildNode("policy");
	}

	/**
	 * @return the location part of the node
	 */
	public ASTNode getAgentLocation() {
		return (ASTNode) this.getChildNode("location");
	}
}
