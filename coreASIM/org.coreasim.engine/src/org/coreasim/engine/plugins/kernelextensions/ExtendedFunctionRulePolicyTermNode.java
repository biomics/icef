/*	
 * ExtendedFunctionRulePolicyTermNode.java
 * 
 * Copyright (C) 2010 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.kernelextensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	This is an {@link ASTNode} for extended Function/Rule Term expressions.
 *   
 *  @author  Roozbeh Farahbod
 */

public class ExtendedFunctionRulePolicyTermNode extends ASTNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

	private List<ASTNode> argsList = null;

    public ExtendedFunctionRulePolicyTermNode(ScannerInfo info) {
        super(
        		KernelExtensionsPlugin.PLUGIN_NAME,
        		ASTNode.EXPRESSION_CLASS,
        		KernelExtensionsPlugin.EXTENDED_FUNC_RULE_POLICY_TERM_NAME,
        		null,
        		info);
    }

    public ExtendedFunctionRulePolicyTermNode(ExtendedFunctionRulePolicyTermNode node) {
    	super(node);
    }
    
    /**
     * Returns the node representing the basic function-rule term
     */
    public ASTNode getTerm() {
        return getFirst();
    }
    
    /**
	 * Returns the list of arguments in a <code>List</code> object.
	 * This method caches the result of its first call, assuming that
	 * the node structure does not change.
	 */
	public List<ASTNode> getArguments() {
		if (argsList == null) {
			List<Node> args = this.getChildNodes("lambda");
			if (args.size() == 0)
				argsList = Collections.emptyList();
			else {
				argsList = new ArrayList<ASTNode>();
				for (Node n: args) 
					if (n instanceof ASTNode)
						argsList.add((ASTNode)n);
			}
		}

		return argsList;
	}
	
    
}
