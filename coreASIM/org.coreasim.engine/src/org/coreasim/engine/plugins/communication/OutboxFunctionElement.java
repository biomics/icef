/*	
 * OutputFunctionElement.java 	1.0 
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.communication;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.plugins.set.SetElement;

/** 
 * This class implements the 'outbox' function provided by the Communication Plugin.
 *   
 * @author  Eric Rothstein
 * 
 */
public class OutboxFunctionElement extends FunctionElement {

	private static final long serialVersionUID = 936788345941442793L;
	private Set<Location> locations;
	/**
	 * Set element containing the messages in the location
	 */
	private SetElement messages;
	
	public OutboxFunctionElement() {
		setFClass(FunctionClass.fcDerived);
		messages =  new SetElement();//new HashSet<MessageElement>();
		locations = new HashSet<Location>();
		locations.add(CommunicationPlugin.OUTBOX_FUNC_LOC);//THIS IS A GLOBAL OUTBOX
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public synchronized Element getValue(List<? extends Element> args) {
		if (args.size() == 0) //FIXME BSL should we allow this? to get the whole mailbox?
			return Element.UNDEF;	//return messages;
		else if (args.size() == 1) //The owner of the outbox. 
		{
			//FIXME BSL this implementation is very inefficient, consider filtering during value setting?
			Set<MessageElement> filteredSet = new HashSet<>(); 
			Set<Element> theSet = messages.getSet();
			for (Element m : theSet)
			{
				if (m instanceof MessageElement)
				{
					MessageElement theMessage = (MessageElement) m;
					String fromAgent = theMessage.getFromAgent();
					if(fromAgent.equals(args.get(0).toString()))
						filteredSet.add(theMessage);
				}
			
			}
			return new SetElement(filteredSet) ;
		}
		else
			return Element.UNDEF;
	}

	/**
	 * Sets the value of this function only if there 
	 * is no argument.
	 */
	@Override
	public synchronized void setValue(List<? extends Element> args, Element value) {
		if (args.size() == 0) {
			if (value instanceof SetElement) 
				{
					messages = (SetElement)value;
				}
			else
			{
				System.out.println("whoops! Something you thought would never happen in OutboxFunctionElement just happened! Search for ERROR#1OutboxFunctionElement to find me.");
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
	/**
	 * Obtains a copy of the messages in the outbox location
	 * @return the set of all messages in the outbox location
	 */
	public synchronized Set<MessageElement> getMessages() {
		Set<MessageElement> theMessages = new HashSet<MessageElement>();
		for (Element e:messages.getSet())
		{
			
			theMessages.add(new MessageElement((MessageElement) e));
		}
		return theMessages;
	}

}
