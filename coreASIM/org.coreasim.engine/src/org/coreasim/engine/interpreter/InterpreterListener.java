/* InterpreterListener.java	1.0
 *   
 * Copyright (C) 2006-2016 The CoreASM Team
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

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.PolicyElement;
import org.coreasim.engine.absstorage.RuleElement;

/**
 *
 * The listener interface for receiving "interesting" interpreter events (node evaluation).
 *
 * The class that is interested in processing a interpreter event either implements this interface (and all the methods it contains).
 *
 * The listener object created from that class is then registered using the ControlAPI's <code>addInterpreterListener</code> method. An interpreter event is generated when the interpreter is evaluating a node. When an interpreter event occurs, the relevant method in the listener object is invoked, and the node is passed to it.
 * 
 * @author Michael Stegmaier
 * @see InterpreterImp#executeTree()
 */
public interface InterpreterListener {
	/**
	 * Invoked before the <code>pos</code> is evaluated by the interpreter.
	 */
    public void beforeNodeEvaluation(ASTNode pos);
    /**
	 * Invoked after the <code>pos</code> has been evaluated by the interpreter.
	 */
    public void afterNodeEvaluation(ASTNode pos);
    /**
     * Invoked on initiating the execution of <code>program</code> by <code>agent</code>.
     */
    public void initProgramExecution(Element agent, RuleElement program);
    
    /**
     * Invoked on initiating the evaluation of <code>policy</code> by <code>agent</code>.
     */
    public void initPolicyExecution(Element agent, PolicyElement policy);
    /**
     * Invoked on rule call.
     */
    public void onRuleCall(RuleElement rule, List<ASTNode> args, ASTNode pos, Element agent);
    
    /**
     * Invoked on policy call.
     */
    public void onPolicyCall(PolicyElement policy, List<ASTNode> args, ASTNode pos, Element agent);
   
    /**
     * Invoked on rule exit.
     */
    public void onRuleExit(RuleElement rule, List<ASTNode> args, ASTNode pos, Element agent);
    
    /**
     * Invoked on policy exit.
     */
    public void onPolicyExit(PolicyElement policy, List<ASTNode> args, ASTNode pos, Element agent);

}
