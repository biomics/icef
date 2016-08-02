/*	
 * SelfFunctionElement.java  	
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 * This file contains source code contributed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS) 
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 * 
 *
 */
 
package org.coreasim.engine.absstorage;

import java.util.List;

/** 
 * The special 'self' function element.
 *   
 * @author  Roozbeh Farahbod
 * 
 * @deprecated 
 */
@Deprecated
public class SelfFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1340097235091309744L;

	/** name of the self function */
	public final static String NAME = "self";
	
	/** location of the self function */
	public final static Location SELF_LOCATION = new Location(NAME, ElementList.NO_ARGUMENT);
	
	private Element agent = Element.UNDEF;
	
	public SelfFunctionElement() {
		this.setSignature(new Signature(0));
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (args.size() == 0)
			return agent;
		else
			return Element.UNDEF;
	}
	
	@Override
	public void setValue(List<? extends Element> args, Element agent) {
		if (args.size() == 0) {
			if (agent != null)
				this.agent = agent;
			else
				throw new NullPointerException("'self' cannot be null.");
		}
	}

}
