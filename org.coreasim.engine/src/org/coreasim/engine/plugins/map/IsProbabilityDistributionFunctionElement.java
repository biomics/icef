/*	
 * IsProbabilityDistribution.java  	$Revision: 243 $
 * 
 * Copyright (C) 2009 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.map;

import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.number.NumberElement;
import org.coreasim.engine.plugins.set.SetBackgroundElement;

/** 
 * A function that creates a collection of pairs from map elements.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class IsProbabilityDistributionFunctionElement extends FunctionElement {
	
	public static final String NAME = "isProbabilityDistribution";
	
	protected Signature signature = null;
	
	public IsProbabilityDistributionFunctionElement() {
		setFClass(FunctionClass.fcDerived);
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + NAME + ".");
		final MapElement map = (MapElement)args.get(0);
		boolean result = true;
		double number = 0;
		for (Element k : map.keySet())
		{
			Element v = map.get(k);
			if (! (v instanceof NumberElement))
			{
				result = false;
				break;
			}
			NumberElement n = (NumberElement) v;
			double theValue  = n.getValue();
			if(theValue < 0)
			{
				result = false;
				break;
			}
			number += theValue;
			//FIXME Float comparisons are fun, yay!
			if(number > 1.001)
			{
				result = false;
				break;
			}
		}
		//FIXME Float comparisons are fun, yay!
		if(number < 0.999)
		{
			result = false;
		}
		if (result)
			return BooleanElement.TRUE;
		else
			return BooleanElement.FALSE;
	}

	@Override
	public Signature getSignature() {
		if (signature == null) {
			signature = new Signature();
			signature.setDomain(MapBackgroundElement.NAME);
			signature.setRange(SetBackgroundElement.SET_BACKGROUND_NAME);
		}
		return signature;
	}
	
	/*
	 * Checks the arguments of the function
	 */
	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 1) && (args.get(0) instanceof MapElement);
	}

}
