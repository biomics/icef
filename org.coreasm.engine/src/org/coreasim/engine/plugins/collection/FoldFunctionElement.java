/*	
 * FoldFunctionElement.java  	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.collection;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;

/** 
 * Function element providing the 'fold' function.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class FoldFunctionElement extends CollectionFunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8062458098713101492L;
	/** suggested names for this function */
	public static final String FOLD_NAME = "fold";
	public static final String FOLDR_NAME = "foldr";
	public static final String FOLDL_NAME = "foldl";
	
	protected final boolean isFoldR;
	private Signature signature = new Signature("ELEMENT", "FUNCTION", "ELEMENT", "ELEMENT");
	
	public FoldFunctionElement(ControlAPI capi, boolean isFoldR) {
		super(capi);
		this.isFoldR = isFoldR;
	}
	
	@Override
	public Signature getSignature() {
		return signature;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + (isFoldR ? FOLDR_NAME : FOLD_NAME) + ".");
		
		Collection<? extends Element> values = ((Enumerable)args.get(0)).enumerate();
		FunctionElement f = (FunctionElement)args.get(1);
		
		Element lastValue = args.get(2);
		
		if (isFoldR) {
			Stack<Element> stack = new Stack<Element>();
			for (Element e: values) 
				stack.push(e);
			while (!stack.isEmpty())
				lastValue = f.getValue(ElementList.create(stack.pop(), lastValue));
			return lastValue;
		} else {
			for (Element e: values) 
				lastValue = f.getValue(ElementList.create(lastValue, e));
			return lastValue;
		}
	}

	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 3) 
				&& (args.get(0) != null && args.get(0) instanceof Enumerable)
				&& (args.get(1) != null && args.get(1) instanceof FunctionElement)
				&& (args.get(2) != null);
	}
}
