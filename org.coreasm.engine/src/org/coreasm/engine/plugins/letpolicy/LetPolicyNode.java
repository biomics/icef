/*	
 * LetPolicyNode.java 	1.0 	$Revision: 243 $
 * 
 * Copyright (C) 2006 George Ma
 * Copyright (C) 2007 Roozbeh Farahbod
 * 
 * Last modified on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $ by $Author: rfarahbod $
 *
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.plugins.letpolicy;

import java.util.HashMap;
import java.util.Map;

import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.ScannerInfo;

/** 
 *	CondtionalPolicyNode is a NodeWrapper for conditional (ifThen) nodes.
 *   
 *  @author  George Ma, Roozbeh Farahbod
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
