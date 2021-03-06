/*	
 * KernelServices.java 	1.0
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
 *
 */
 
package org.coreasim.engine.kernel;

import org.codehaus.jparsec.Parser;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.plugin.PluginServiceInterface;

/** 
 * Provides kernel-specific services to other plugins.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class KernelServices implements PluginServiceInterface {

	private final Kernel kernel;
	
	protected KernelServices(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * @return the Rule parser hook from the Kernel plugin
	 */
	public Parser<Node> getRuleParser() {
		return kernel.getParser("Rule");
	}
	
	/**
	 * @return the Rule parser hook from the Kernel plugin
	 */
	public Parser<Node> getPolicyParser() {
		return kernel.getParser("Policy");
	}

	/**
	 * @return the Guard parser hook from the Kernel plugin
	 */
	public Parser<Node> getGuardParser() {
		return kernel.getParser("Term");
	}

	/**
	 * @return the Term parser hook from the Kernel plugin
	 */
	public Parser<Node> getTermParser() {
		return kernel.getParser("Term");
	}

	/**
	 * @return the BasicTerm parser hook from the Kernel plugin
	 */
	public Parser<Node> getBasicTermParser() {
		return kernel.getParser("BasicTerm");
	}

	/**
	 * @return the BasicExpr parser hook from the Kernel plugin
	 */
	public Parser<Node> getBasicExprParser() {
		return kernel.getParser("BasicExpr");
	}

	/**
	 * @return the ConstantTerm parser hook from the Kernel plugin
	 */
	public Parser<Node> getConstantTermParser() {
		return kernel.getParser("ConstantTerm");
	}

	/**
	 * @return the FunctionRulePolicyTerm parser hook from the Kernel plugin
	 */
	public Parser<Node> getFunctionRulePolicyTermParser() {
		return kernel.getParser("FunctionRulePolicyTerm");
	}
	/**
	 * @return the FunctionPolicyTerm parser hook from the Kernel plugin
	 */
	public Parser<Node> getFunctionPolicyTermParser() {
		return kernel.getParser("FunctionPolicyTerm");
	}

	/**
	 * @return the Header parser hook from the Kernel plugin
	 */
	public Parser<Node> getHeaderParser() {
		return kernel.getParser("Header");
	}

	/**
	 * @return the RuleSignature parser hook from the Kernel plugin
	 */
	public Parser<Node> getRuleSignatureParser() {
		return kernel.getParser("RuleSignature");
	}

	/**
	 * @return the TupleTerm parser hook from the Kernel plugin
	 */
	public Parser<Node> getTupleTermParser() {
		return kernel.getParser("TupleTerm");
	}
	
	/**
	 * Returns the parser component of the kernel that is associated with the 
	 * given grammar rule. Calling this rule makes sense only after
	 * the Kernel has gathered all the plugin components (e.g., after parsing).
	 * 
	 * @param grammarRule name of a grammar rule
	 * @return the parser component of the kernel that is associated with the 
	 * 			given grammar rule
	 */
	public Parser<Node> getParserComponent(String grammarRule) {
		return kernel.getParsers().get(grammarRule).parser;
	}
	
}
