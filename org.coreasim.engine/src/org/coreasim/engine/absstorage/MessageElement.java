/*	
 * MessageElement.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.absstorage;

import org.coreasim.engine.plugins.choosepolicy.ChoosePolicyPlugin.Pair;

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
	/**
	 * Returns the Element payload of the message element
	 * @return the message payload 
	 */
	public Element getMessage() {
		return message;
	}
	/**
	 * Returns the destination Agent of the message element
	 * @return the destination agent
	 */
	public String getToAgent() {
		return toAgent;
	}
	/**
	 * Returns the subject of the message element
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * Sets the message payload
	 * @param the message payload
	 */
	public void setMessage(Element message) {
		this.message = message;
	}
	/**
	 * Sets the destination agent
	 * @param the destination Agent toAgent
	 */
	public void setToAgent(String toAgent) {
		this.toAgent = toAgent;
	}
	
	/**
	 * Sets the origin Agent
	 * @param fromAgent the origin agent
	 */
	public void setFromAgent(String fromAgent) {
		this.fromAgent = fromAgent;
	}
	/**
	 * Sets the subject 
	 * @param subject the subject of the message element
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * Sets the stepcount 
	 * @param stepcount the stepcount
	 */
	public void setStepcount(int stepcount) {
		this.stepcount = stepcount;
	}
	/**
	 * Returns the stepcount of the message element
	 * @return the stepcount
	 */
	public int getStepcount() {
		return stepcount;
	}
	/**
	 * Returns the origin agent of the message element
	 * @return the origin agent
	 */
	public String getFromAgent() {
		return fromAgent;
	}
	/**
	 * The payload 
	 */
	private Element message;
	/**
	 * The destination agent
	 */
	private String toAgent;
	/**
	 * The origin agent
	 */
	private String fromAgent;
	/**
	 * The subject
	 */
	private String subject;
	/**
	 * The stepcount
	 */
	private int stepcount;
	/**
	 * the type //Could be removed?
	 */
	private String type;

	/**
	 * Default constructor
	 */
	public MessageElement()
	{
		this.fromAgent = "";
		this.message = Element.UNDEF;
		this.toAgent = "";
		this.subject = "";
		this.stepcount = -1;
	}
/**
 * Builds a copy of the given Message Element
 * @param e the message element to be copied
 */
	public MessageElement(MessageElement e) {
        fromAgent = e.getFromAgent();
        toAgent = e.getToAgent();
        subject = e.getSubject();
        message = e.getMessage();
        stepcount = e.getStepcount();
        type = e.getType();
    }
	/**
	 * Conditioned constructor. Requires correct types in order to construct the message element
	 * @param fromAgent The destination agent (cannot be null)
	 * @param message the payload of the message (cannot be null)
	 * @param toAgent the origin agent (cannot be null)
	 * @param subject the subject (cannot be null)
	 * @param stepcount the stepcount 
	 * @param type the type
	 */
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
		this.type =type;
	}

	public String toString() {
		String result = "from "+fromAgent;
		result +=" to "+toAgent;
		result +=" of type "+type;
		result +=" with subject '"+subject+"'";
		result +=" at step "+stepcount;
		result +=": "+message;
		return result;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object anElement)
	{
		if (anElement == null)
			return false;
 		if (anElement instanceof MessageElement)
 			{
 				MessageElement otherMessage = (MessageElement) anElement;
 				return otherMessage.getMessage().equals(this.getMessage()) &&
 					   otherMessage.getSubject().equals(this.getSubject()) &&
 					  otherMessage.getStepcount() == this.getStepcount() &&
 					 otherMessage.getFromAgent().equals(this.getFromAgent()) &&
 					 otherMessage.getToAgent().equals(this.getToAgent());
 				
 			}
 		else
 			return ((Element)this).equals(anElement);
	}
	@Override
	public MessageElement clone() {
		MessageElement clone = null; 
		try{ clone = (MessageElement) super.clone(); }
		catch(CloneNotSupportedException e){ 
			throw new RuntimeException(e); // won't happen 
			} 
		return clone; }
}
