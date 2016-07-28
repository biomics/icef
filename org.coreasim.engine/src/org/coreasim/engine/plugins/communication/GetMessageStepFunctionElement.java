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
 
package org.coreasim.engine.plugins.communication;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * This class implements the 'output' function provided by the IO Plugin.
 *   
 * @author  Roozbeh Farahbod
 * 
 * @see org.coreasim.engine.plugins.io.IOPlugin
 */
public class GetMessageStepFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	public GetMessageStepFunctionElement() {
		setFClass(FunctionClass.fcDerived);
		//this.capi = capi;
		//this.value = Element.UNDEF;		
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 0) //FIXME BSL should we allow this? to get the whole mailbox?
			return Element.UNDEF;//	return messages;
		else if (args.size() == 1) //has to be a message element 
		{
			Element arg0 = args.get(0);
			if (arg0 instanceof MessageElement)
			{
				MessageElement theMessage = (MessageElement)arg0;
				return NumberElement.getInstance(theMessage.getStepcount());
			}else
			{
				return Element.UNDEF;
			}
		}
		else
			return Element.UNDEF;
	}
}
