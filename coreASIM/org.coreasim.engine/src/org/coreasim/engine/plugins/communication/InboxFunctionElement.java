/*	
 * InboxFunctionElement.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.communication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.interpreter.SelfAgent;
import org.coreasim.engine.plugins.set.SetElement;

/** 
 * This class implements the 'inbox' function
 *   
 * @author  Eric Rothstein
 * 
 */
public class InboxFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -782541721957242779L;
	private Set<Location> locations;
	private SetElement messages;
	private ControlAPI capi;
	
	public InboxFunctionElement(ControlAPI capi) {
		setFClass(FunctionClass.fcMonitored);
		messages = new SetElement();
		locations = new HashSet<Location>();
		locations.add(CommunicationPlugin.INBOX_FUNC_LOC);//THIS IS A GLOBAL INBOX
		this.capi =capi;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public synchronized Element getValue(List<? extends Element> args) {
		if (args.size() == 0) //FIXME BSL should we allow this? to get the whole mailbox?
			return new SetElement(messages.getSet()) ;
			//return Element.UNDEF;//	return messages;
		else if (args.size() == 1) //The owner of the inbox. 
		{
			//FIXME BSL this implementation is very inefficient, consider filtering during value setting?
			Set<Element> theSet = messages.getSet();
			Set<MessageElement> filteredSet = new HashSet<>(); 
			for (Element m : theSet)
			{
				if (m instanceof MessageElement)
				{
					MessageElement theMessage = (MessageElement) m;
					 String toAgent = theMessage.getToAgent();
					 Element checker = args.get(0);
					 Element selfAgent = capi.getScheduler().getSelfAgent();
					if (checker.toString().equals(capi.getScheduler().getSelfAgent().toString()))
					{
						if(toAgent.equals(((SelfAgent) selfAgent).getExternalName())||toAgent.equals(checker.toString()))
							filteredSet.add(theMessage);	
					}
					else if(toAgent.equals(checker.toString()))
						filteredSet.add(theMessage);	
				}else
				{
					System.out.println("InboxFunctionElement is storing non MessageElements! Search for ERROR#2InboxFunctionElement to find me.");
				}
			}
			return new SetElement(filteredSet) ;
		}
		else
			return Element.UNDEF;
	}

	/**
	 * Transforms the incoming set of elements into the value of the inbox location
	 */
	@Override
	public synchronized void setValue(List<? extends Element> args, Element value) {
		if (args.size() == 0) {
			if (value instanceof SetElement) 
				messages = (SetElement)value;
			else
			{
				System.out.println("whoops! Something you thought would never happen in InboxFunctionElement just happened! Search for ERROR#1InboxFunctionElement to find me.");
				//FIXME DO NOTHING?
				//outputValues = new StringElement(value.toString());
			}
		}
	}

	/**
	 * Parameter <code>name</code> is ignored.
	 * 
	 * @see FunctionElement#getLocations(String)
	 */
	public Set<Location> getLocations(String name) {
		return locations;
	}

}
