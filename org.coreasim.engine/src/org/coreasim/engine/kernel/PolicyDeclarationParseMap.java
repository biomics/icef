/*	
 * PolicyDeclarationParseMap.java 	1.0
 * 
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;
import org.coreasim.engine.parser.ParseMapN;

/** 
 * A parser map for policy declaration nodes.
 *   
 * @author Eric Rothstein
 * 
 */
public class PolicyDeclarationParseMap extends ParseMapN<Node> {

	public PolicyDeclarationParseMap() {
		super(Kernel.PLUGIN_NAME);
	}
	
	public Node map(Object... vals) {
		ScannerInfo info = null;
		info = ((Node)vals[0]).getScannerInfo();
		
		Node node = new ASTNode(
				null,
				ASTNode.DECLARATION_CLASS,
				Kernel.GR_POLICYDECLARATION,
				null,
				info
				);

		for (int i=0; i < vals.length; i++) {
			Node child = (Node)vals[i];
			if (child != null)
				// to give proper names to ASTNode children:
				if (child instanceof ASTNode) {
					if (((ASTNode)child).getGrammarClass().equals("PolicySignature"))
						node.addChild("alpha", child);
					else
						node.addChild("beta", child);
				} else
					node.addChild(child);
		}
		
		return node;
	}

}
