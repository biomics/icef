/*	
 * InitializationParseMap.java 	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.kernel;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;
import org.coreasim.engine.parser.ParseMapN;

/** 
 * A parser map for the initialization declaration.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class InitializationParseMap extends ParseMapN<Node> {

	public InitializationParseMap() {
		super(Kernel.PLUGIN_NAME);
	}
	
	public Node map(Object... vals) {
		ScannerInfo info = null;
		if (vals.length > 0)
			info = ((Node)vals[0]).getScannerInfo();
		
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
