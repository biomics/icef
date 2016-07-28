/*	
 * HasCyclesFunctionElement.java 
 * 
 * Copyright (C) 2010 Roozbeh Farahbod
 *
 * Last modified by $Author$ on $Date$.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
package org.coreasm.network.plugins.graph;

import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.BooleanBackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;

/**
 *  Test if the given graph has cycles.
 * 
 * @author Roozbeh Farahbod
 *
 */
public class HasCyclesFunctionElement extends FunctionElement {

	Signature sig = null;
	final CycleDetectorCache detectorCache;
	
	public static final String FUNCTION_NAME = "hasCycle";
	
	public HasCyclesFunctionElement(CycleDetectorCache detectorCache) {
		this.detectorCache = detectorCache;
	}
	
	@Override
	public FunctionClass getFClass() {
		return FunctionClass.fcDerived;
	}

	@Override
	public Signature getSignature() {
		if (sig == null)
			sig = new Signature(GraphBackgroundElement.BACKGROUND_NAME,
				BooleanBackgroundElement.BOOLEAN_BACKGROUND_NAME);
		return sig;
	}

	@Override
	public Element getValue(List<? extends Element> args) {
		if (!(args.size() == 1 && args.get(0) instanceof GraphElement))
			throw new CoreASIMError("Illegal arguments for " + FUNCTION_NAME + ".");
		
		Graph<Element, Element> g = ((GraphElement)args.get(0)).getGraph();
		CycleDetector<Element, Element> detector = detectorCache.getCycleDetector(g);
				
		if (detector != null)
			return BooleanElement.valueOf(detector.detectCycles());
		
		return Element.UNDEF;
	}

}
