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

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 926274521917609653L;
	public static final String NORMAL_TYPE = "normal";
	public static final String CREATION_TYPE = "creation";

	public Element getMessage() {
		return message;
	}

	public String getToAgent() {
		return toAgent;
	}

	public String getSubject() {
		return subject;
	}

	public void setMessage(Element message) {
		this.message = message;
	}

	public void setToAgent(String toAgent) {
		this.toAgent = toAgent;
	}

	public void setFromAgent(String fromAgent) {
		this.fromAgent = fromAgent;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setStepcount(int stepcount) {
		this.stepcount = stepcount;
	}

	public int getStepcount() {
		return stepcount;
	}

	public String getFromAgent() {
		return fromAgent;
	}

	private Element message;
	private String toAgent;
	private String fromAgent;
	private String subject;
	private int stepcount;
	
	public MessageElement()
	{
		this.fromAgent = "";
		this.message = Element.UNDEF;
		this.toAgent = "";
		this.subject = "";
		this.stepcount = -1;
	}

	public MessageElement(String fromAgent, Element message, String toAgent, String subject, int stepcount, String type) {
		if (fromAgent == null)
			throw new NullPointerException("Cannot create a MessageElement with a null sender");
		if (message == null)
			throw new NullPointerException("Cannot create a MessageElement with a null message");
		if (subject == null)
			throw new NullPointerException("Cannot create a MessageElement with a null subject");
		if (toAgent == null)
			throw new NullPointerException("Cannot create a MessageElement with a null receiver");
		this.fromAgent = fromAgent;
		this.message = message;
		this.toAgent = toAgent;
		this.subject = subject;
		this.stepcount = stepcount;
	}

	public String toString() {
		String result = "from "+fromAgent;
		result +=" to "+toAgent;
		result +=" with subject '"+subject+"'";
		result +=" at step "+stepcount;
		result +=": "+message;
		return result;
	}
}
