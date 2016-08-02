/*	
 * SchedulePrimitiveParseMap.java 	1.0
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
import org.coreasim.engine.parser.ParseMap;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugins.communication.CreateAgentRuleNode;

/** 
 * A parser map for the scheduling policy form.
 *   
 * @author Eric Rothstein
 * 
 */
public class SchedulePrimitiveParseMap extends ParserTools.ArrayParseMap {


	String nextChildName = "alpha";
	public SchedulePrimitiveParseMap() {
		super(Kernel.PLUGIN_NAME);
	}

	public Node map(Object[] vals) {
		nextChildName = "alpha";
        Node node = new SchedulePrimitiveNode(((Node)vals[0]).getScannerInfo());
        addChildren(node, vals);
		return node;
	}
	@Override
	public void addChild(Node parent, Node child) {
		if (child instanceof ASTNode)
			parent.addChild(nextChildName, child);
		else {
			String token = child.getToken();
	        if (token.equals(Kernel.KW_SCHEDULE))
	        	nextChildName = "id";
	        else if (token.equals(Kernel.KW_WITH))
        		nextChildName = "content";
	        else if (token.equals(Kernel.KW_IN))
	        	nextChildName = "location";
			super.addChild(parent, child);
		}
	}
	
}
