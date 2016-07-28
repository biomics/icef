/*	
 * SubGraphFunctionElement.java 
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coreasim.engine.CoreASMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.set.SetBackgroundElement;
import org.coreasim.util.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedSubgraph;

/**
 * Computes the sub-graph of the given graph for a given set of vertices.
 * 
 * @author Roozbeh Farahbod
 *
 */
public class SubGraphFunctionElement extends FunctionElement {

	Signature sig = null;

	public static final String FUNCTION_NAME = "subgraph";
	
	@Override
	public FunctionClass getFClass() {
		return FunctionClass.fcDerived;
	}

	@Override
	public Signature getSignature() {
		if (sig == null)
			sig = new Signature(GraphBackgroundElement.BACKGROUND_NAME,
				SetBackgroundElement.SET_BACKGROUND_NAME,
				GraphBackgroundElement.BACKGROUND_NAME);
		return sig;
	}

	@Override
	public Element getValue(List<? extends Element> args) {
		if (!(args.size() == 2 && args.get(0) instanceof GraphElement && args.get(1) instanceof Enumerable))
			throw new CoreASMError("Illegal arguments for " + FUNCTION_NAME + ".");
		
		GraphElement ge = (GraphElement)args.get(0);
		Collection<? extends Element> vs = ((Enumerable)args.get(1)).enumerate();
		Set<Element> vset = new HashSet<Element>(vs);

		if (ge.isDirected()) {
			return new DirectedGraphElement(new DirectedSubgraph<Element, Element>(
					(DirectedGraph<Element, Element>)ge.getGraph(), vset, null));
		} else
			Logger.log(Logger.WARNING, Logger.plugins, "subgraph is not supported on undirected graphs.");
		
		return Element.UNDEF;
	}

}
