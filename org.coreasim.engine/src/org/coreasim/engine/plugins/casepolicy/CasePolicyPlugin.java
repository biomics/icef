/*	
 * CasePolicyPlugin.java 	1.0 	
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.casepolicy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.casepolicy.CompilerCasePolicyPlugin;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.TriggerMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;

/** 
 *	Plugin for case policy.
 *   
 *  @author  Eric Rothstein
 *  
 */
public class CasePolicyPlugin extends Plugin 
    implements ParserPlugin, InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 1, 1, "beta");
	
	public static final String PLUGIN_NAME = CasePolicyPlugin.class.getSimpleName();
	
	public static final String CASE_ITEM_POLICY_DELIMITER = ":";

	private final String[] keywords = {"case", "of", "endcase"};
	private final String[] operators = {CASE_ITEM_POLICY_DELIMITER};
	
    private Map<String, GrammarRule> parsers = null;
    private ThreadLocal<Map<Node,Set<ASTNode>>> matchingPolicies;

    private final CompilerPlugin compilerPlugin = new CompilerCasePolicyPlugin(this);
    
    @Override
    public void initialize() {
        matchingPolicies = new ThreadLocal<Map<Node, Set<ASTNode>>>() {
			@Override
			protected Map<Node, Set<ASTNode>> initialValue() {
				return new IdentityHashMap<Node, Set<ASTNode>>();
			}
        };
    }

	public String[] getKeywords() {
		return keywords;
	}

	public String[] getOperators() {
		return operators;
	}

	/**
	 * @return <code>null</code>
	 */
	public Parser<Node> getParser(String nonterminal) {
		return null;
	}

    public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> policyParser = kernel.getPolicyParser();
			Parser<Node> termParser = kernel.getTermParser();
			
			ParserTools pTools = ParserTools.getInstance(capi);
			
			Parser<Node> casePolicyParser = Parsers.array(
				new Parser[] {
					pTools.getKeywParser("case", PLUGIN_NAME),
					termParser,
					pTools.getKeywParser("of", PLUGIN_NAME),
					pTools.plus(
					pTools.seq(
						termParser,
						pTools.getOprParser(CASE_ITEM_POLICY_DELIMITER),
						policyParser)
					),
					pTools.getKeywParser("endcase", PLUGIN_NAME)
				}).map(
				new CaseParseMap());
			
				parsers.put("Policy",
					new GrammarRule("CasePolicy",
							"'case' Term 'of' (Term '" + CASE_ITEM_POLICY_DELIMITER + "' Policy)+ 'endcase'", 
							casePolicyParser, PLUGIN_NAME));
		}
		return parsers;
	}

	/* (non-Javadoc)
     * @see org.coreasm.engine.Plugin#interpret(org.coreasm.engine.interpreter.Node)
     */
    public ASTNode interpret(Interpreter interpreter, ASTNode pos) {
       
        if (pos instanceof CasePolicyNode) {
            CasePolicyNode caseNode = (CasePolicyNode) pos;
            
            if (!caseNode.getCaseTerm().isEvaluated()) {
            	// clear the cache of the policies whose guard 
            	// will match the value of the case term
            	matchingPolicies.get().remove(caseNode);
            	// return the case term for evaluation
            	return caseNode.getCaseTerm();
            } else {
            	Map<ASTNode, ASTNode> caseMap = new IdentityHashMap<ASTNode, ASTNode>();
            	caseMap = caseNode.getCaseMap();
            	
            	// evaluate all case guards
            	for (ASTNode guard: caseMap.keySet()) {
            		if (!guard.isEvaluated())
            			return guard;
            	}
            	
            	Set<ASTNode> matchingPolicies = this.matchingPolicies.get().get(caseNode);
            	if (matchingPolicies == null) {
            		matchingPolicies = new HashSet<ASTNode>();
            		this.matchingPolicies.get().put(caseNode, matchingPolicies);
            	}
            	
            	// At this point, all guards are evaluated
            	// It's time to evaluate policies with a matching guard
            	for (Entry<ASTNode, ASTNode> pair: caseMap.entrySet()) {
            		Element value = pair.getKey().getValue();
            		if (value == null) {
            			capi.error("Case guard does not have a value.", pair.getKey(), interpreter);
            			return pos;
            		}
        			if (!pair.getValue().isEvaluated()) 
        				if (value.equals(caseNode.getCaseTerm().getValue())) {
        					// add this policy to the cache
        					matchingPolicies.add(pair.getValue());
        					return pair.getValue(); 
        				}
            	}
            	
            	// At this point all matching policies are evaluated
            	// Time to put all the triggers together
            	TriggerMultiset result = new TriggerMultiset();
            	for (ASTNode trigger: matchingPolicies) {
            		result.addAll(trigger.getTriggers());
            	}
            	
            	pos.setNode(null, null, result, null);
            	return pos;
            	
            }
        }
        else {
            return null;
        }
    }

	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}
	
	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	public static class CaseParseMap //extends ParseMapN<Node> {
	extends ParserTools.ArrayParseMap {
		String nextChildName;
		
		public CaseParseMap() {
			super(PLUGIN_NAME);
		}

		public Node map(Object[] vals) {
			nextChildName = "alpha";
            Node node = new CasePolicyNode(((Node)vals[0]).getScannerInfo());
            addChildren(node, vals);
			return node;
		}

		@Override
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode) {
				parent.addChild(nextChildName, child);
				if (nextChildName.equals("gamma"))
					nextChildName = "beta";
			} else {
				parent.addChild(child);
				if (child.getToken().equals("of")) 				// case item 
					nextChildName = "beta";
				else
					if (child.getToken().equals(CASE_ITEM_POLICY_DELIMITER))		// case policy
						nextChildName = "gamma";
			}
		}
	}
	
	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}
}
