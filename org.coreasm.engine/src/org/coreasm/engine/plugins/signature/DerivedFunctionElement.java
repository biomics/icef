/*	
 * DerivedFunctionElement.java  	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 * Last modified by $Author: erothst $ on $Date: 2016-04-14 23:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.plugins.signature;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.coreasm.engine.ControlAPI;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.Interpreter;
import org.coreasm.engine.interpreter.InterpreterException;

/** 
 * Derived functions keep a copy of the expression tree and call the 
 * interpreter to evaluate the subtree everytime they are asked for
 * their value.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class DerivedFunctionElement extends FunctionElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5733580433068402147L;
	protected final ControlAPI capi;
	protected final List<String> params;
	protected final ASTNode expr;
	protected final Map<ASTNode, ASTNode> exprCopies = new IdentityHashMap<ASTNode, ASTNode>();
	protected final String name;
	
	/**
	 * Creates a new derived function with the given list 
	 * of parameters.
	 */
	public DerivedFunctionElement(ControlAPI capi, String name, List<String> params, ASTNode expr) {
		this.capi = capi;
		this.name = name;
		this.params = Collections.unmodifiableList(params);
		this.expr = expr;
		setFClass(FunctionClass.fcDerived);
	}
	
	/*
	 * @see org.coreasm.engine.absstorage.FunctionElement#getValue(java.util.List)
	 */
	@Override
	public Element getValue(List<? extends Element> args) {
		Element result = Element.UNDEF;
		if (args.size() == params.size()) {
			Interpreter interpreter = capi.getInterpreter().getInterpreterInstance();
			bindArguments(interpreter, args);
			
			synchronized(this) {
				ASTNode exprCopy = exprCopies.get(interpreter.getPosition());
				if (exprCopy == null) {
					exprCopy = (ASTNode)interpreter.copyTree(expr);
					exprCopies.put(interpreter.getPosition(), exprCopy);
				}
				try {
					interpreter.interpret(exprCopy, interpreter.getSelf());
					if (exprCopy.getValue() != null)
						result = exprCopy.getValue();
				} catch (InterpreterException e) {
					capi.error(e, expr, interpreter);
				} finally {
					unbindArguments(interpreter);
				}
				interpreter.clearTree(exprCopy);
			}
		}
		
		return result;
	}

	protected void bindArguments(Interpreter interpreter, List<? extends Element> values) {
		interpreter.hideEnvVars();
		for (int i=0; i < params.size(); i++)
			interpreter.addEnv(params.get(i), values.get(i));
	}
	
	protected void unbindArguments(Interpreter interpreter) {
		for (int i=0; i < params.size(); i++)
			interpreter.removeEnv(params.get(i));
		interpreter.unhideEnvVars();
	}
	
	@Override
	public String toString(){
		return "";
	}

	/**
	 * @return the params
	 */
	public List<String> getParams() {
		return params;
	}

	/**
	 * @return the expr
	 */
	public ASTNode getExpr() {
		return expr;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
}
