/*	
 * SchedulePrimitiveParseMap.java 	$Revision: 243 $
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
 
package org.coreasm.engine.kernel;

import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.Node;
import org.coreasm.engine.parser.ParseMap;
import org.coreasm.engine.parser.ParserTools;
import org.coreasm.engine.plugins.communication.CreateAgentRuleNode;

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
