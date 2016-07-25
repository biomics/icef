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

import org.coreasm.engine.ControlAPI;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.plugins.set.SetElement;
import org.coreasm.engine.plugins.string.StringElement;

/** 
 * This class implements the 'output' function provided by the IO Plugin.
 *   
 * @author  Roozbeh Farahbod
 * 
 * @see org.coreasm.engine.plugins.io.IOPlugin
 */
public class GetMessageSenderFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	public GetMessageSenderFunctionElement() {
		setFClass(FunctionClass.fcDerived);		
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 0)
			return Element.UNDEF;
		else if (args.size() == 1) //has to be a message element 
		{
			Element arg0 = args.get(0);
			if (arg0 instanceof MessageElement)
			{
				MessageElement theMessage = (MessageElement)arg0;
				return new StringElement(theMessage.getFromAgent());
			}else
			{
				return Element.UNDEF;
			}
		}
		else
			return Element.UNDEF;
	}
}
