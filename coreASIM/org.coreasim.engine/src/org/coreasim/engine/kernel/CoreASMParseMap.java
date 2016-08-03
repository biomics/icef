/*	
 * CoreASMParseMap.java 	
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
 *
 */
 
package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;
import org.coreasim.engine.parser.ParseMapN;

/** 
 * A parser map for the top CoreASM grammar rule.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class CoreASMParseMap extends ParseMapN<Node> {

	public CoreASMParseMap() {
		super(Kernel.PLUGIN_NAME);
	}
	
	public Node map(Object... vals) {
		ScannerInfo info = null;
		
		// consider the possiblity of starting with a 
		// comment or whitespace
		if (vals[0] != null && ((Node)vals[0]).getToken().equals("CoreASIM"))
			info = ((Node)vals[0]).getScannerInfo();
		else
			info = ((Node)vals[1]).getScannerInfo();
		
		ASTNode rootNode = new ASTNode(
				pluginName, 
				"CoreASIM", 
				Kernel.GR_COREASM, 
				null, 
				info
				);
		rootNode.setParent(null);

		addChildren(rootNode, vals);
		
		return rootNode;
	}

}
