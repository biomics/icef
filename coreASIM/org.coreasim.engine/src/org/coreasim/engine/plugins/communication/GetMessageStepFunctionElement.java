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

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.plugins.number.NumberElement;

/** 
 * This class implements the 'GetMessageStep' function provided by the Communication Plugin.
 *   
 * @author  Eric Rothstein
 * 
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
