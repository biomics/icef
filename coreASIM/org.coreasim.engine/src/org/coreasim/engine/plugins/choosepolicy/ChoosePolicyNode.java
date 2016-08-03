/*	
 * ChoosePolicyNode.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.choosepolicy;

import java.util.HashMap;
import java.util.Map;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;
import org.coreasim.engine.plugins.chooserule.ChooseRulePlugin;

/** 
 *	ChoosePolicyNode is a Node for choose policy nodes.
 *   
 *  @author  Eric Rothstein
 *  
 */

public class ChoosePolicyNode extends ASTNode {

    private static final long serialVersionUID = 1L;
    
    public ChoosePolicyNode(ChoosePolicyNode node) {
		super(node);
	}

	public ChoosePolicyNode(ScannerInfo scannerInfo) {
		super(ChoosePolicyPlugin.PLUGIN_NAME, 
				ASTNode.POLICY_CLASS, 
				"ChoosePolicy", 
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
            	throw new CoreASIMError("Variable \""+current.getToken()+"\" already defined in choose policy.", this);
        }
        
        return variableMap;
    }

    /**
     * Returns the node representing the 'do' part of the choose policy.
     */
    public ASTNode getDoPolicy() {
        return (ASTNode)getChildNode(ChoosePolicyPlugin.DO_POLICY_NAME);
    }
    
    /**
     * Returns the node representing the 'ifnone' part of the choose policy.
     */
    public ASTNode getIfnonePolicy() {
        return (ASTNode)getChildNode(ChoosePolicyPlugin.IFNONE_POLICY_NAME);
    }
    
    /**
     * Returns the node representing the condition ('with' part) of the choose policy.
     * null is returned if the choose policy has no condition.
     */
    public ASTNode getCondition() {
    	return (ASTNode)getChildNode(ChoosePolicyPlugin.GUARD_NAME);
    }
    
    public ASTNode getDistribution() {
		 return (ASTNode)getChildNode(ChoosePolicyPlugin.DISTRIBUTION_NAME);
	}
    
}
