/*	
 * SchedulePrimitive.java 	1.0 	$Revision: 243 $
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
 
package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 * Scheduling policy node.
 *   
 * @author  Eric Rothstein
 * 
 */
public class SchedulePrimitiveNode extends ASTNode {

	private static final long serialVersionUID = 1L;

	public SchedulePrimitiveNode(SchedulePrimitiveNode node) {
		super(node);
	}
	
	public SchedulePrimitiveNode(ScannerInfo info) {
		super(null,
				ASTNode.POLICY_CLASS,
				"SchedulePrimitive",
				null,
				info
				);
	}
	
	/**
	 * @return the agent to be scheduled
	 */
	public Node getAgent() {
		return this.getChildNode("id");
	}
	
	/**
	 * @return the content of the scheduling signal
	 */
	public Node getContent() {
		return this.getChildNode("content");
	}

	
	/**
	 * @return the location of the scheduling signal
	 */
	public Node getSubject() {
		return this.getChildNode("location");
	}

}
