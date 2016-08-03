/*	
 * PolicyElement.java 	1.0 	
 * 
* This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.absstorage;

import java.util.Collections;
import java.util.List;

import org.coreasim.engine.interpreter.ASTNode;

import java.util.ArrayList;

/** 
 *	This class implements a named ASM policy.
 *   
 *  @author  Eric Rothstein
 *  
 */
public class PolicyElement extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3597578052999058488L;

	/**
	 * Declaration node in the parse tree.
	 */
	public final ASTNode node;
	
	/**
	 * The body of this rule
	 */
	private ASTNode body = null;
	
	/**
	 * Parameter tokens of this rule
	 */
	private List<String> param = null;
	
	/** Name of the policy at the time of declaration. */
	public final String name;
	
	/**
	 * Creates a new policy with a 
	 * list of parameter tokens (<code>String</code>)
	 * and a policy body.
	 * 
	 * @param node declaration node of this policy
	 * @param name name of this policy
	 * @param param list of parameter tokens
	 * @param body body of the policy
	 */
	public PolicyElement(ASTNode node, String name, List<String> param, ASTNode body) {
		super();
		this.node = node;
		setBody(body);
		setParam(param);
		this.name = name;
	}

	public String getBackground() {
		return PolicyBackgroundElement.POLICY_BACKGROUND_NAME;
	}
	
	/**
	 * @return the declaration node of this policy in the parse tree
	 */
	public ASTNode getDeclarationNode() {
		return node;
	}
	
	/**
	 * Gets the body of this policy.
	 * 
	 * @return Returns the body.
	 */
	public org.coreasim.engine.interpreter.ASTNode getBody() {
		return body;
	}

	/**
	 * Sets the body of this policy.
	 * 
	 * @param body The body to set.
	 */
	public void setBody(org.coreasim.engine.interpreter.ASTNode body) {
		if (body == null) 
			throw new IllegalArgumentException("Body of a policy cannot be null.");
		else
			this.body = body;
	}

	/** Returns the original name of this policy */
	public String getName() {
		return name;
	}

	/**
	 * Returns the parameter tokens list of this
	 * policy.
	 * 
	 * @return Returns the param.
	 */
	public List<String> getParam() {
		return param;
	}

	/**
	 * Sets the parameter tokens of this policy. 
	 * If <code>param</code> is null, an empty
	 * list will be used instead.
	 * 
	 * @param param The param to set.
	 */
	public void setParam(List<String> param) {
		if (param == null || param.size() == 0)
			this.param = Collections.emptyList();
		else
			this.param = Collections.unmodifiableList(new ArrayList<String>(param));
	}

	public String toString() {
		//return "@" + this.getName();
		return //"rule "+this.getName()+" = "+
				this.getBody().unparseTree();
	}
}
