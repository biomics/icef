/*	
 * FilterFunctionElement.java  	1.0
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
 
package org.coreasim.engine.plugins.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;

/** 
 * Function element providing the 'filter' function.
 * This function expects two arguments:
 * <ul>
 * <li>A collection <b>c</b> of elements; i.e., an instance of {@link AbstractMapElement}</li>
 * <li>A filtering function <b>f</b> of the form <i>f: Element -> BooleanElement</i>; 
 * i.e., an instance of {@link FunctionElement} 
 * </ul>
 * 
 * The result will be a collection, with the same type as <b>c</b>, of the those
 * elements in <b>c</b> for which <b>f</b> returns {@link BooleanElement#TRUE}.
 * 
 * If the arguments are not as expected, this method returns {@link Element#UNDEF}.
 * 
 * @author  Roozbeh Farahbod
 * 
 */
public class FilterFunctionElement extends CollectionFunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5810311608252014922L;

	/** suggested name for this function */
	public static final String NAME = "filter";
	
	private Signature signature = new Signature("ELEMENT", "FUNCTION", "ELEMENT");
	
	public FilterFunctionElement(ControlAPI capi) {
		super(capi);
	}

	@Override
	public Signature getSignature() {
		return signature;
	}
	
	/**
	 * See the description for {@link FilterFunctionElement}.
	 * 
	 * @see org.coreasim.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		
		Collection<? extends Element> values = ((Enumerable)args.get(0)).enumerate();
		FunctionElement f = (FunctionElement)args.get(1);
		Collection<Element> resultValues = new ArrayList<Element>();		
		for (Element e: values) {
			Element fValue = f.getValue(ElementList.create(e));
			if (fValue instanceof BooleanElement)
				if (((BooleanElement)fValue).getValue())
					resultValues.add(e);
		}
		
		return ((AbstractMapElement)args.get(0)).getNewInstance(resultValues);
	}

	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 2) 
				&& (args.get(0) != null && args.get(0) instanceof AbstractMapElement)
				&& (args.get(1) != null && args.get(1) instanceof FunctionElement);
	}
}
