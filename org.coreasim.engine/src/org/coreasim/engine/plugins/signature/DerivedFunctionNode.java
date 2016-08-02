/*  
 * DerivedFunctionNode.java    1.0
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.signature;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	This node represents derived function definitions.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public class DerivedFunctionNode extends ASTNode {

	private static final long serialVersionUID = 1L;

    
    public DerivedFunctionNode(ScannerInfo info) {
        super(
        		SignaturePlugin.PLUGIN_NAME,
        		ASTNode.DECLARATION_CLASS,
        		"DerivedFunctionDeclaration",
        		null,
        		info);
    }

    public DerivedFunctionNode(DerivedFunctionNode node) {
    	super(node);
    }
    
    /**
     * @return the name signature of the function
     */
    public ASTNode getNameSignatureNode() {
        return this.getFirst();
    }
    
    /**
     * @return the expression
     */
    public ASTNode getExpressionNode() {
        return getFirst().getNext();
    }
    
    @Override
    public String unparse()
    {
		return getExpressionNode().unparse();
    	
    }
        
}
