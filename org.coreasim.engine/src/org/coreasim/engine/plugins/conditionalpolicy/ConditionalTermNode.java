/*
 * ConditionalTermNode.java		1.0
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */

package org.coreasim.engine.plugins.conditionalpolicy;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/**
 * A node representing a conditional term
 * @author Eric Rothstein
 *
 */
@SuppressWarnings("serial")
public class ConditionalTermNode extends ASTNode {

	public ConditionalTermNode(ScannerInfo info) {
		super(ConditionalPolicyPlugin.PLUGIN_NAME, ASTNode.EXPRESSION_CLASS, "ConditionalTerm", null, info);
	}

    public ConditionalTermNode(ConditionalTermNode node) {
    	super(node);
    }
    
    public ASTNode getCondition() {
        return getFirst();
    }
    
    public ASTNode getIfTerm() {
        return getCondition().getNext();
    }
    
    public ASTNode getElseTerm() {
        return getIfTerm().getNext();
    }
}
