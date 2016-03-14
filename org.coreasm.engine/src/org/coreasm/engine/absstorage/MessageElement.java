/*	
 * MessageElement.java 	1.0 	$Revision: 80 $
 * 
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
 * 
 * Last modified by $Author: rfarahbod $ on $Date: 2009-07-24 16:25:41 +0200 (Fr, 24 Jul 2009) $.
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.absstorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coreasm.engine.plugins.choosepolicy.ChoosePolicyPlugin.Pair;

/** 
 *	The element representing a Universe in the state.
 *   
 *  @author  Roozbeh Farahbod
 */
public class MessageElement extends Element {

	
	public String getMessage() {
		return message;
	}

	public Element getToAgent() {
		return toAgent;
	}

	public Element getFromAgent() {
		return fromAgent;
	}

	private String message;
	private Element toAgent;
	private Element fromAgent;

	public MessageElement(Element fromAgent, String message, Element toAgent) {
		if (fromAgent == null)
			throw new NullPointerException("Cannot create a MessageElement with a null sender");
		if (message == null)
			throw new NullPointerException("Cannot create a MessageElement with a null message");
		if (toAgent == null)
			throw new NullPointerException("Cannot create a MessageElement with a null receiver");
		this.fromAgent = fromAgent;
		this.message = message;
		this.toAgent = toAgent;
	}

	public String toString() {
		String result = "from "+fromAgent.toString();
		result +=" to "+toAgent.toString();
		result +=": "+message;
		return result;
	}
}
