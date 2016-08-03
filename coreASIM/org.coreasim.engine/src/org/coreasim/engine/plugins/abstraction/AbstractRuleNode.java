/*	
 * AbstractRuelNode.java 	1.0 	
 * 
 * Copyright (C) 2006-2007 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 * This file contains source code contributed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS) 
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 * 
 */
 
package org.coreasim.engine.plugins.abstraction;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 * A node for Abstract Rule nodes.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class AbstractRuleNode extends ASTNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractRuleNode(ScannerInfo info) {
		super(
				AbstractionPlugin.PLUGIN_NAME,
				ASTNode.RULE_CLASS,
				"AbstractRule",
				null,
				info);
	}
	
	public AbstractRuleNode(AbstractRuleNode node) {
		super(node);
	}

	/**
	 * @return the message part of this node
	 */
	public ASTNode getMessage() {
		return this.getFirst();
	}
}
