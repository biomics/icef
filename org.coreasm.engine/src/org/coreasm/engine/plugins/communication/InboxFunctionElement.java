/*	
 * OutputFunctionElement.java 	1.0 	$Revision: 243 $
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.plugins.communication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.plugins.set.SetElement;

/** 
 * This class implements the 'output' function provided by the IO Plugin.
 *   
 * @author  Roozbeh Farahbod
 * 
 * @see org.coreasm.engine.plugins.io.IOPlugin
 */
public class InboxFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -782541721957242779L;
	private Set<Location> locations;
	private SetElement messages;
	
	public InboxFunctionElement() {
		setFClass(FunctionClass.fcMonitored);
		messages = new SetElement();
		locations = new HashSet<Location>();
		locations.add(CommunicationPlugin.INBOX_FUNC_LOC);//THIS IS A GLOBAL INBOX
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 0) //FIXME BSL should we allow this? to get the whole mailbox?
			return Element.UNDEF;//	return messages;
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
					if(toAgent.equals(args.get(0).toString()))
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
	 * Sets the value of this function only if there 
	 * is no argument.
	 */
	@Override
	public void setValue(List<? extends Element> args, Element value) {
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
