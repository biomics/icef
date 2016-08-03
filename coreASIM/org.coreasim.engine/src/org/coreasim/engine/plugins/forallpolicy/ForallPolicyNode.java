/*	
 * ForallPolicyNode.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.forallpolicy;

import java.util.HashMap;
import java.util.Map;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	ForallPolicyNode is a NodeWrapper for forall policy nodes.
 *   
 *  @author  Eric Rothstein
 *  
 */

public class ForallPolicyNode extends ASTNode {

    /**
     * Creates a new ForallPolicyNode
     */
    public ForallPolicyNode(ScannerInfo info) {
        super(
        		ForallPolicyPlugin.PLUGIN_NAME,
        		ASTNode.POLICY_CLASS,
        		"ForallPolicy",
        		null,
        		info);
    }

    public ForallPolicyNode(ForallPolicyNode node) {
    	super(node);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Returns a map of the variable names to the nodes which
     * represent the domains that variable should be taken from
     * @throws CoreASIMError 
     */
    public Map<String,ASTNode> getVariableMap() throws CoreASIMError {
    	Map<String,ASTNode> variableMap = new HashMap<String,ASTNode>();
        
        for (ASTNode current = getFirst(); current.getNext() != null && current.getNext().getNext() != null && ASTNode.ID_CLASS.equals(current.getGrammarClass()); current = current.getNext().getNext()) {
            if (variableMap.put(current.getToken(),current.getNext()) != null)
            	throw new CoreASIMError("Variable \""+current.getToken()+"\" already defined in forall policy.", this);
        }
        
        return variableMap;
    }
    
    /**
     * Returns the node representing the 'do' part of the forall policy.
     */
    public ASTNode getDoPolicy() {
        return (ASTNode)getChildNode("policy");   
    }
    
    /**
     * Returns the node representing the 'ifnone' part of the forall policy.
     */
    public ASTNode getIfnonePolicy() {
        return (ASTNode)getChildNode("ifnone");
    }
    
    /**
     * Returns the node representing the 'with' part of the forall policy.
     * If there is no 'with' condition specified, null is returned.
     */
    public ASTNode getCondition() {
    	return (ASTNode)getChildNode("guard"); 
    }

}
