/*	
 * PickExpNode.java 	1.0 	
 * 
 *
 * Copyright (C) 2008 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.chooserule;

import java.util.HashMap;
import java.util.Map;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	This is an {@link ASTNode} for pick-from expressions.
 *   
 *  @author  Roozbeh Farahbod
 */

public class PickExpNode extends ASTNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PickExpNode(ScannerInfo info) {
        super(
        		ChooseRulePlugin.PLUGIN_NAME,
        		ASTNode.EXPRESSION_CLASS,
        		"PickExp",
        		null,
        		info);
    }

    public PickExpNode(PickExpNode node) {
    	super(node);
    }
    
    /**
     * Returns the node representing the bound variable of the 'pick' expression
     */
    public ASTNode getVariable() {
        return getFirst();
    }
    
    /**
     * Returns the node representing the domain of the 'pick' expression
     */
    public ASTNode getDomain() {
        return getVariable().getNext();
    }
    
    /**
     * Returns the node representing the condition ('with' part) of the choose rule.
     * null is returned if the choose rule has no condition.
     */
    public ASTNode getCondition() {
    	return (ASTNode)getChildNode(ChooseRulePlugin.GUARD_NAME);
    }

	public ASTNode getDistribution() {
		 return (ASTNode)getChildNode(ChooseRulePlugin.DISTRIBUTION_NAME);
	}
}
