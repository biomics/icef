/*	
 * LetPolicyNode.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.letpolicy;

import java.util.HashMap;
import java.util.Map;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	CondtionalPolicyNode is a NodeWrapper for conditional (ifThen) nodes.
 *   
 *  @author  Eric Rothstein
 *  
 */
public class LetPolicyNode extends ASTNode {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new LetPolicyNode
     */
    public LetPolicyNode(ScannerInfo info) {
        super(
        		LetPolicyPlugin.PLUGIN_NAME,
        		ASTNode.POLICY_CLASS,
        		"LetPolicy",
        		null,
        		info);
    }

    public LetPolicyNode(LetPolicyNode node) {
    	super(node);
    }
    
    /**
     * Returns a map of the variable names to the nodes which
     * represent the terms that will be aliased
     * @throws Exception 
     */
    public Map<String,ASTNode> getVariableMap() throws Exception {
    	Map<String,ASTNode> variableMap = new HashMap<String,ASTNode>();
        
        ASTNode current = getFirst();
        
        while (current.getNext() != null) {
            if (variableMap.keySet().contains(current.getToken())) {
                throw new Exception("Token \""+current.getToken()+"\" already defined in let policy.");
            }
            else {
                variableMap.put(current.getToken(),current.getNext());
            }
            current = current.getNext().getNext();
        }
        return variableMap;
    }
       
    /**
     * Returns the node representing the 'in' part the let policy.
     */
    public ASTNode getInPolicy() {
        return (ASTNode)getChildNode("gamma");
    }
    
}
