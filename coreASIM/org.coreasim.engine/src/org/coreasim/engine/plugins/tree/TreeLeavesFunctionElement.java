/*	
 * TreeLeavesFunctionElement.java
 * 
 * Copyright (C) 2010 Dipartimento di Informatica, Universita` di Pisa, Italy.
 *
 * Author: Franco Alberto Cardillo 		(facardillo@gmail.com)
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.tree;

import java.util.List;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Signature;
import org.coreasim.engine.plugins.list.ListBackgroundElement;
import org.coreasim.engine.plugins.list.ListElement;


/** 
 * Function returning the leaves of the tree rooted in the passed node.
 *   
 * @author  Franco Alberto Cardillo
 * 
 */
public class TreeLeavesFunctionElement extends FunctionElement {
	
	public static final String TREE_LEAVES_FUNC_NAME =  TreePlugin.TREE_PREFIX + "Leaves";
	
	
	protected Signature signature = null;
	
	
	public TreeLeavesFunctionElement() {
		setFClass(FunctionClass.fcMonitored);
	} // TreeLeavesFunctionElement

	/* (non-Javadoc)
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		if (!checkArguments(args))
			throw new CoreASIMError("Illegal arguments for " + TREE_LEAVES_FUNC_NAME + ".");
		
		TreeNodeElement node = (TreeNodeElement) args.get(0);
		return new ListElement(node.getLeaves());
	}

	@Override
	public Signature getSignature() {
		if (signature == null) {
			signature = new Signature();
			signature.setDomain(TreeBackgroundElement.TREE_BACKGROUND_NAME);
			signature.setRange(ListBackgroundElement.LIST_BACKGROUND_NAME);
		} // if
		return signature;
	} // getSignature
	
	/*
	 * Checks the arguments of the function
	 */
	protected boolean checkArguments(List<? extends Element> args) {
		return (args.size() == 1) && (args.get(0) instanceof TreeNodeElement);
	} // checkArguments

} // TreeNodeValueFunctionElement.java
