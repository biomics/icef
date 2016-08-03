/*
 * ApplicationTermParseMap.java 		1.0 
 * 
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
 *
 */

package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.parser.ParseMapN;

/**
 * Parse map for application term parser.
 *   
 * @author Roozbeh Farahbod
 *
 */

public class ApplicationTermParseMap extends ParseMapN<Node> {

	public ApplicationTermParseMap() {
		super(Kernel.PLUGIN_NAME);
	}
	
	public Node map(Object... vals) {
		Node node = new ApplicationTermNode((Node)vals[0]);
		node.addChild("alpha", (Node)vals[0]); // FunctionRulePolicyTerm
		
		for (int i=1; i < vals.length; i++) {
			if (vals[i] != null && vals[i] instanceof ASTNode) {
				// Then it should be a TupleTerm
				for (Node n: ((Node)vals[i]).getChildNodes())
					if (n instanceof ASTNode) 
						node.addChild("lambda", n);
					else 
						node.addChild(n);
			}
		}
		return node;
	}

}
