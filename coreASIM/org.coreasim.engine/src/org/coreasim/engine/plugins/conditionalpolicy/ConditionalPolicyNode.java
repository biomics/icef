/*	
 * ConditionalPolicyNode.java 	1.0 	
 * 
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
 *	CondtionalPolicyNode is a node for conditional (ifThen) nodes.
 *   
 * @author  Eric Rothstein
 * 
 */
public class ConditionalPolicyNode extends ASTNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConditionalPolicyNode
     */
    public ConditionalPolicyNode(ScannerInfo info) {
        super(ConditionalPolicyPlugin.PLUGIN_NAME, 
        		ASTNode.POLICY_CLASS,
        		"ConditionalPolicy",
        		null,
        		info);
    }

    public ConditionalPolicyNode(ConditionalPolicyNode node) {
    	super(node);
    }
    
    /**
     * Returns the node representing the guard of the conditional policy
     */
    public ASTNode getGuard() {
        return getFirst();
    }
    
    /**
     * Returns the node representing the consequent of the conditional policy
     * (i.e. policy to evaluate if the guard is true)
     */
    public ASTNode getIfPolicy() {
        return getGuard().getNext();
    }
    
    /**
     * Returns the node representing the 'else' part the conditional policy.
     * (i.e. policy to evaluate if the guard is false)
     * This value may be null.
     */
    public ASTNode getElsePolicy() {
        return getIfPolicy().getNext();
    }
}
