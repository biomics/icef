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

	public ASTNode getAgentInit() {
		return (ASTNode) this.getChildNode("init");
	}
	
	public ASTNode getAgentProgram() {
		return (ASTNode) this.getChildNode("program");
	}
	
	public ASTNode getAgentPolicy() {
		return (ASTNode) this.getChildNode("policy");
	}
	
	public ASTNode getAgentLocation() {
		return (ASTNode) this.getChildNode("location");
	}
}
