/*	
 * Interpreter.java 	1.0 	
 * 
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
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
 *
 */
 
package org.coreasim.engine.interpreter;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.PolicyElement;
import org.coreasim.engine.absstorage.RuleElement;

/** 
 *	Defines the interface of the interpreter.
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public interface Interpreter {

	/**
	 * Executes the program at <i>pos</i>. After a successful 
	 * call to this method, <i>pos</i> must be evaluated.
	 */
	public void executeTree() throws InterpreterException;
	
	/**
	 * Returns <code>true</code> if the program execution is finished.
	 * <i>(parent(pos) == null)</i>
	 */
	public boolean isExecutionComplete();
	
	/**
	 * Sets the value of "pos" to the given node. 
	 * This method does not make a duplicate copy 
	 * of the node, and as a result, is not thread-safe.
	 * 
	 * In a multi-threaded environment, the caller should 
	 * send a non-shared copy of the node to this method (perhaps
	 * by duplicating it using {@link #copyTree(Node)}). 
     *  
	 * @param pos the node to be interpreted
	 */
	public void setPosition(ASTNode pos);
 
	/**
	 * Gets the current value of "pos"
	 *  
	 */
	public ASTNode getPosition();

	/**
	 * Sets the value of 'self' for this interpreter instance.
	 * 
	 * @param newSelf new value of 'self'
	 */
	public void setSelf(Element newSelf);
	
	/**
	 * Sets the value of 'self' for this interpreter instance for the evaluation of the policy
	 * 
	 * @param newSelf new value of 'self'
	 */
	public void setSelfForPolicy(Element environmentAgent, PolicyElement policy);
	
	/**
	 * Returns the current value of self for this interpreter.
	 * 
	 */
	public Element getSelf();
	
	/**
	 * Clears the result of evaluation on the given tree.
	 * 
	 * @param root root of a tree to be cleared
	 */
	public void clearTree(ASTNode root);

	/**
	 * Returns a copy of the given parse tree, where every instance 
	 * of an identifier node in a given sequence (formal parameters) 
	 * is substituted by a copy of the corresponding parse tree in another 
	 * sequence (actual parameters, or arguments). We assume that the elements in the 
	 * formal parameters list are all distinct (i.e., it is not possible 
	 * to specify the same name for two different parameters).
	 * 
	 * @param root root of the parse tree
	 * @param params formal parameters
	 * @param args given arguments (replace parameters in the tree)
	 */
	public ASTNode copyTreeSub(ASTNode root, List<String> params, List<ASTNode> args);
	
	/**
	 * Makes a deep copy of a sub tree with its root at <code>a</code>.
	 * All the connected nodes (except the parent node) are duplicated.
	 * 
	 * @param a root of a tree
	 * @return a copy of the tree 
	 */
	public Node copyTree(Node a);

	/**
	 * Prepares the initial state.
	 */
	public void prepareInitialState();
	
	/**
	 * Prepares the interpreter for evaluation of a program. The most important
	 * task is clearing the tree of the program (i.e., removing previous evaluation
	 * results).
	 */
	public void initProgramExecution();
	
	/**
	 * Prepares the interpreter for evaluation of a program. The most important
	 * task is clearing the tree of the program (i.e., removing previous evaluation
	 * results).
	 * @param policy 
	 */
	public void initPolicyExecution(PolicyElement policy);

	/**
	 * Handles a call to a rule.
	 * 
	 * @param rule rule element
	 * @param params parameters
	 * @param args arguments
	 * @param pos current node being interpreted
	 * @return a node to be interpreted next
	 */
	public ASTNode ruleCall(RuleElement rule, List<String> params, List<ASTNode> args, ASTNode pos);

	/**
	 * Handles a call to a policy.
	 * 
	 * @param policy policy element
	 * @param params parameters
	 * @param args arguments
	 * @param pos current node being interpreted
	 * @return a node to be interpreted next
	 */
	public ASTNode policyCall(PolicyElement policy, List<String> params, List<ASTNode> args, ASTNode pos);

	/**
	 * Creates a new scope for the environment variable
	 * with the given name and assigns its value. 
	 * 
	 * @param name name of the variable
	 * @param value value of the variable
	 */
	public void addEnv(String name, Element value);
	
	/**
	 * Returns a copy of the current environment variables.
	 * @return a copy of the current environment variables
	 */
	public Map<String, Element> getEnvVars();
	
	/**
	 * Returns the top-most value of the 
	 * environment variable with the given name.
	 */
	public Element getEnv(String name);

	/**
	 * Removes the top-most instance of the
	 * environment variable with the given name.
	 * 
	 * @param name name of the variable
	 */
	public void removeEnv(String name);
	
	/**
	 * Hide the current environment variables
	 */
	public void hideEnvVars();
	
	/**
	 * Unhide the previously hidden environment variables
	 */
	public void unhideEnvVars();
	
	/**
	 * Returns a copy of the current call stack.
	 */
	public Stack<CallStackElement> getCurrentCallStack();
	
	/*
	 * Sets the value of <i>env(token)</i>.
	 *
	public void setEnv(String token, Element value);
	*/
	
	/**
	 * Returns an instance of this interpreter registered
	 * for the running thread.
	 */
	public Interpreter getInterpreterInstance();
	
    /**
     * Interprets (evaluates) the given ASTNode.
     * 
     * This method may not be thread-safe. 
     * 
     * @param node The {@link ASTNode} to be evaluated
     * @param agent The agent evaluating that node.
     * 
     * @throws InterpreterException 
     */
    public void interpret(ASTNode node, Element agent) throws InterpreterException;

    /**
     * Cleans up any cached data in the interpreter.
     * 
     */
    public void cleanUp();
    
    public void dispose();
    
	/**
	 * 
	 * Holds the information about an entry in the rule call stack.
	 *   
	 * @author Roozbeh Farahbod
	 *
	 */
	public class CallStackElement {
		public final RuleElement rule;
		public final PolicyElement policy;
		
		protected CallStackElement(RuleElement r, PolicyElement p) {
			rule = r;
			policy = p;
		}
		
		public String toString() {
			String ruleResult = "null";
			String policyResult = "null";
			if (rule!=null){
				String ruleParams = rule.getParam().toString();
				ruleParams = "(" + ruleParams.substring(1, ruleParams.length() - 1) + ")";
				ruleResult =rule.name + ruleParams;
			}
			if (policy!=null){
				String policyParams = policy.getParam().toString();
				policyParams = "(" + policyParams.substring(1, policyParams.length() - 1) + ")";
				policyResult =policy.name + policyParams;
			}
			return ruleResult +" and "+policyResult; 
		}
		
		public boolean equals(Object o) {
			if (o instanceof CallStackElement) {
				CallStackElement cse = (CallStackElement)o;
				if(rule!=null && cse.rule!=null)
				{
					return this.rule.equals(cse.rule);
				}
				else if(policy!=null && cse.policy!=null)
				{
					return this.policy.equals(cse.policy) ;
				}
				else 
					return false;
				
			} else
				return false;
		}
		
		public int hashCode() {
			if (rule!=null)
				return rule.hashCode();
			else
				return policy.hashCode();
		}
	}
}
