/*	
 * PolicySignatureParseMap.java 	1.0
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
 * A parser map for the PolicySignature grammar rule.
 *   
 * @author Eric Rothstein
 * 
 */
public class PolicySignatureParseMap extends ParseMapN<Node> {

	public PolicySignatureParseMap() {
		super(Kernel.PLUGIN_NAME);
	}
	
	public Node map(Object... vals) {
		ScannerInfo info = null;
		if (vals.length > 0)
			info = ((Node)vals[0]).getScannerInfo();
		
		Node node = new ASTNode(
						pluginName,
						ASTNode.DECLARATION_CLASS,
						"PolicySignature",
						null,
						info
						);		
		addChildren(node, vals);

		return node;
	}

}
