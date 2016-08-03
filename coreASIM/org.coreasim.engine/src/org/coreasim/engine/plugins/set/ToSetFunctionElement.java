/*	
 * ToSetFunctionElement.java  	1.0
 * 
 * Copyright (C) 2008 Roozbeh Farahbod
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
 
package org.coreasim.engine.plugins.set;

import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementBackgroundElement;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;

/** 
 * Converts {@link Enumerable}s to {@link SetElement}s.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class ToSetFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6275831422286421359L;

	public static final String NAME = "toSet";
	
	protected Signature signature = null;

	public ToSetFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}
	
	public Signature getSignature() {
		if (signature == null) {
			signature = new Signature();
			signature.setDomain(ElementBackgroundElement.ELEMENT_BACKGROUND_NAME);
			signature.setRange(SetBackgroundElement.SET_BACKGROUND_NAME);
		}
		return signature;
	}
	
	/**
	 * If args contains only one instance of {@link Enumerable}, 
	 * this method returns a {@link SetElement} view of that 
	 * enumerable.
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!(args.size() == 1 && args.get(0) instanceof Enumerable))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		return new SetElement(((Enumerable)args.get(0)).enumerate());
	}

}
