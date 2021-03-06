/*	
 * IsConnectedFunctionElement.java 
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
package org.coreasim.network.plugins.graph;

import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.jgrapht.DirectedGraph;

/**
 *  Returns an undirected version of the given graph.
 *  If the given graph is undirected, returns the same graph element.
 * 
 * @author Roozbeh Farahbod
 *
 */
public class AsUndirectedFunctionElement extends FunctionElement {

	Signature sig = null;
	
	public static final String FUNCTION_NAME = "asUndirectedGraph";
	
	public AsUndirectedFunctionElement() {}
	
	@Override
	public FunctionClass getFClass() {
		return FunctionClass.fcDerived;
	}

	@Override
	public Signature getSignature() {
		if (sig == null)
			sig = new Signature(GraphBackgroundElement.BACKGROUND_NAME,
				GraphBackgroundElement.BACKGROUND_NAME);
		return sig;
	}

	@Override
	public Element getValue(List<? extends Element> args) {
		if (!(args.size() == 1 && args.get(0) instanceof GraphElement))
			throw new CoreASIMError("Illegal arguments for " + FUNCTION_NAME + ".");
		
		GraphElement ge = (GraphElement)args.get(0);
		if (ge instanceof DirectedGraphElement)
			return new UndirectedGraphElement((DirectedGraph<Element, Element>)ge.getGraph());
		if (ge instanceof UndirectedGraphElement)
			return ge;
		
		return Element.UNDEF;
	}

}
