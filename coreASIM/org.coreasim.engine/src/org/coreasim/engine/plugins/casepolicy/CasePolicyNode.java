/*	
 * CasePolicyNode.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.casepolicy;

import java.util.IdentityHashMap;
import java.util.Map;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	A parse node for case policies.
 *   
 * @author Eric Rothstein
 * 
 */
public class CasePolicyNode extends ASTNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new CaseRuleNode
     */
    public CasePolicyNode(ScannerInfo info) {
        super(CasePolicyPlugin.PLUGIN_NAME, 
        		ASTNode.POLICY_CLASS,
        		"CasePolicy",
        		null,
        		info);
    }

    public CasePolicyNode(CasePolicyNode node) {
    	super(node);
    }
    
    public ASTNode getCaseTerm() {
    	return (ASTNode)getChildNode("alpha");
    }
    
    /**
     * Returns a map of case guards to their corresponding policies
     * 
     * @throws Exception 
     */
    public Map<ASTNode, ASTNode> getCaseMap() {
    	Map<ASTNode, ASTNode> caseMap = new IdentityHashMap<ASTNode, ASTNode>();
         
        ASTNode current = (ASTNode)getChildNode("beta");
        
        while (current != null) {
        	caseMap.put(current,current.getNext());
            current = current.getNext().getNext();
        }
        return caseMap;
    }

}
