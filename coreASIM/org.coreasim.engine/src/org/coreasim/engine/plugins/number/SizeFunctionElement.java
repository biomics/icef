/*	
 * SizeFunctionElement.java  	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
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
 */
 
package org.coreasim.engine.plugins.number;

import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;

/** 
 * Impelements the 'size' function.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class SizeFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8981403715177185627L;
	public static final String NAME = "size";
	
	public SizeFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		return getValue((Enumerable)args.get(0));
	}

	/**
	 * Returns the size of the given enumerable as a 
	 * {@link NumberElement}.
	 * 
	 * @param e an {@link Enumerable}
	 */
	public Element getValue(Enumerable e) {
		if (e.size() == Long.MAX_VALUE)
			return NumberElement.POSITIVE_INFINITY;
		return NumberElement.getInstance(e.size());
	}
	
	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 1) 
				&& (args.get(0) instanceof Enumerable);
	}

}
