/*	
 * ChooseRuleNode.java 	1.0 	
 * 
 * Copyright (C) 2006 George Ma
 * Copyright (c) 2007 Roozbeh Farahbod
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
 *	ChooseRuleNode is a Node for choose rule nodes.
 *   
 *  @author  George Ma, Roozbeh Farahbod
 *  
 */

public class ChooseRuleNode extends ASTNode {

    private static final long serialVersionUID = 1L;
    
    public ChooseRuleNode(ChooseRuleNode node) {
		super(node);
	}

	public ChooseRuleNode(ScannerInfo scannerInfo) {
		super(ChooseRulePlugin.PLUGIN_NAME, 
				ASTNode.RULE_CLASS, 
				"ChooseRule", 
				null, 
				scannerInfo);
	}
	
	/**
     * Returns a map of the variable names to the nodes which
     * represent the domains that variable should be taken from
     * @throws CoreASIMError 
     */
    public Map<String,ASTNode> getVariableMap() throws CoreASIMError {
    	Map<String,ASTNode> variableMap = new HashMap<String,ASTNode>();
        
        for (ASTNode current = getFirst(); current.getNext() != null && current.getNext().getNext() != null && ASTNode.ID_CLASS.equals(current.getGrammarClass()); current = current.getNext().getNext()) {
            if (variableMap.put(current.getToken(),current.getNext()) != null)
            	throw new CoreASIMError("Variable \""+current.getToken()+"\" already defined in choose rule.", this);
        }
        
        return variableMap;
    }

    /**
     * Returns the node representing the 'do' part of the choose rule.
     */
    public ASTNode getDoRule() {
        return (ASTNode)getChildNode(ChooseRulePlugin.DO_RULE_NAME);
    }
    
    /**
     * Returns the node representing the 'ifnone' part of the choose rule.
     */
    public ASTNode getIfnoneRule() {
        return (ASTNode)getChildNode(ChooseRulePlugin.IFNONE_RULE_NAME);
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
