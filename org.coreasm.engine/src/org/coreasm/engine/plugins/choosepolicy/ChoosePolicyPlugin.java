/*	
 * ChoosepolicyPlugin.java 	1.0 	$Revision: 243 $
 *
 * Last modified on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $ by $Author: rfarahbod $
 * 
 * Copyright (C) 2006 George Ma
 * Copyright (c) 2007 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.plugins.choosepolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasm.compiler.interfaces.CompilerPlugin;
import org.coreasm.compiler.plugins.choosepolicy.CompilerChoosePolicyPlugin;
import org.coreasm.engine.ControlAPI;
import org.coreasm.engine.CoreASMError;
import org.coreasm.engine.VersionInfo;
import org.coreasm.engine.absstorage.BooleanElement;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.Enumerable;
import org.coreasm.engine.absstorage.TriggerMultiset;
import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.Interpreter;
import org.coreasm.engine.interpreter.InterpreterException;
import org.coreasm.engine.interpreter.Node;
import org.coreasm.engine.kernel.KernelServices;
import org.coreasm.engine.parser.GrammarRule;
import org.coreasm.engine.parser.ParserTools;
import org.coreasm.engine.plugin.InterpreterPlugin;
import org.coreasm.engine.plugin.ParserPlugin;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.util.Tools;

/** 
 *	Plugin for choose policy
 *   
 *  @author  George Ma, Roozbeh Farahbod
 *  
 */
public class ChoosePolicyPlugin extends Plugin implements ParserPlugin,
        InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 9, 3, "");
	
	public static final String PLUGIN_NAME = ChoosePolicyPlugin.class.getSimpleName();
	
	protected static final String GUARD_NAME = "guard";
	protected static final String DO_POLICY_NAME = "dopolicy";
	protected static final String IFNONE_POLICY_NAME = "ifnonepolicy";

	private final String[] keywords = {"choose", "pick", "with", "in", "do", "ifnone", "endchoose"};
	private final String[] operators = {};
	
    private ThreadLocal<Map<Node,List<Element>>> remained;

    private Map<String, GrammarRule> parsers;
    
    private final CompilerPlugin compilerPlugin = new CompilerChoosePolicyPlugin(this);
    
    @Override
    public CompilerPlugin getCompilerPlugin(){
    	return compilerPlugin;
    }
    
    @Override
    public void initialize() {
        remained = new ThreadLocal<Map<Node, List<Element>>>() {
			@Override
			protected Map<Node, List<Element>> initialValue() {
				return new IdentityHashMap<Node, List<Element>>();
			}
        };
    }

    private Map<Node, List<Element>> getRemainedMap() {
    	return remained.get();
    }
    
	@Override
	public void setControlAPI(ControlAPI capi) {
		super.setControlAPI(capi);
	}

	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}
	
	/**
	 * @return <code>null</code>
	 */
	public Parser<Node> getParser(String nonterminal) {
		return null;
	}


	public String[] getKeywords() {
		return keywords;
	}

	public String[] getOperators() {
		return operators;
	}

	public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> policyParser = kernel.getPolicyParser();
			Parser<Node> termParser = kernel.getTermParser();
			Parser<Node> guardParser = kernel.getGuardParser();
			
			ParserTools npTools = ParserTools.getInstance(capi);
			Parser<Node> idParser = npTools.getIdParser();
			
			// ChoosePolicy : 'choose' ID 'in' Term (',' ID 'in' Term)* ('with' Guard)? 'do' Policy ('ifnone' Policy)? ('endchoose')?
			Parser<Node> choosePolicyParser = Parsers.array(
					npTools.getKeywParser("choose", PLUGIN_NAME),
					npTools.csplus(Parsers.array(idParser,
							npTools.getKeywParser("in", PLUGIN_NAME),
							termParser)),
					npTools.seq(
							npTools.getKeywParser("with", PLUGIN_NAME),
							guardParser).optional(),
					npTools.getKeywParser("do", PLUGIN_NAME),
					policyParser, 
					npTools.seq(
							npTools.getKeywParser("ifnone", PLUGIN_NAME),
							policyParser).optional(),
					npTools.getKeywParser("endchoose", PLUGIN_NAME).optional()).map(
					new ChooseParseMap());
			parsers.put("Policy", 
					new GrammarRule("Policy",
							"'choose' ID 'in' Term (',' ID 'in' Term)* ('with' Guard)? 'do' Policy ('ifnone' Policy)? ('endchoose')?", choosePolicyParser, this.getName()));
			
		}
		return parsers;
	}

    public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
        
        if (pos instanceof ChoosePolicyNode) {
            ChoosePolicyNode chooseNode = (ChoosePolicyNode) pos;
            // Here, we follow the specification of the choose policy
            // and for a more readable code, we clearly distinguish between various
            // forms of choose
            
            // CASE 1. 'choose X in E do P'  
            if (chooseNode.getCondition() == null && chooseNode.getIfnonePolicy() == null) 
            	return interpretChoosePolicy_NoCondition_NoIfnone(interpreter, pos);
   
            // CASE 2. 'choose X in E do P1 ifnone P2'
            if (chooseNode.getCondition() == null && chooseNode.getIfnonePolicy() != null)
            	return interpretChoosePolicy_NoCondition_WithIfnone(interpreter, pos);
     
            // CASE 3. 'choose X in E with C do P'  
            if (chooseNode.getCondition() != null && chooseNode.getIfnonePolicy() == null) 
            	return interpretChoosePolicy_WithCondition_NoIfnone(interpreter, pos);
   
            // CASE 4. 'choose X in E with C do P1 ifnone P2'
            if (chooseNode.getCondition() != null && chooseNode.getIfnonePolicy() != null)
            	return interpretChoosePolicy_WithCondition_WithIfnone(interpreter, pos);
        }
        // in case of error
        	return pos;
    }
    
	/*
     * Interpreting policy of the form: 'choose x in E do R'
     */
	private ASTNode interpretChoosePolicy_NoCondition_NoIfnone(Interpreter interpreter, ASTNode pos) {
        ChoosePolicyNode chooseNode = (ChoosePolicyNode) pos;
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated())
        		return domain;
        }
        
    	// if policy is not evaluated
    	if (!chooseNode.getDoPolicy().isEvaluated()) {
    		boolean none = false;
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	    		if (variable.getValue().getValue() instanceof Enumerable) {
	            	// s := enumerate(v)
	    			Enumerable domain = (Enumerable) variable.getValue().getValue();
	    			List<Element> s = null;
	    			if (domain.supportsIndexedView())
	    				s = domain.getIndexedView();
	    			else 
	    				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                if (s.size() > 0) {
	                    // choose t in s
	                	//TODO BSL Here is where the probability distribution will need to be introduced
	                	int i = Tools.randInt(s.size());
	                    Element chosen = s.get(i);
	                    // AddEnv(x,t)s
	                    interpreter.addEnv(variable.getKey(), chosen);
	                }
	                else {
	                	none = true;
	                	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                }
	            }
	            else {
	                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
	                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
	                return pos;
	            }
    		}
    		if (none) {
    			for (String x : variableMap.keySet())
        			interpreter.removeEnv(x);
    			// [pos] := (undef,{},undef)
                pos.setNode(null, null, new TriggerMultiset(), null);
                return pos;
    		}
    		// pos := gamma
            return chooseNode.getDoPolicy();
    	}
    	
    	// if policy 'P' is evaluated as well
    	else {
            // RemoveEnv(x)
    		for (String x : variableMap.keySet())
    			interpreter.removeEnv(x);
            // [pos] := (undef,u,undef)
            pos.setNode(null,null,chooseNode.getDoPolicy().getTriggers(), null);
            return pos;
    	}
	}

	
	/*
     * Interpreting policy of the form: 'choose x in E do R1 ifnone R2'
     */
    private ASTNode interpretChoosePolicy_NoCondition_WithIfnone(Interpreter interpreter, ASTNode pos) {
        ChoosePolicyNode chooseNode = (ChoosePolicyNode) pos;
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated())
        		return domain;
        }
        
    	// if neither of the policies 'P1' or 'P2' are evaluated
    	if (!chooseNode.getDoPolicy().isEvaluated() && !chooseNode.getIfnonePolicy().isEvaluated()) { 
    		boolean none = false;
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	    		if (variable.getValue().getValue() instanceof Enumerable) {
	            	// s := enumerate(v)
	    			Enumerable domain = (Enumerable) variable.getValue().getValue();
	    			List<Element> s = null;
	    			if (domain.supportsIndexedView())
	    				s = domain.getIndexedView();
	    			else 
	    				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                if (s.size() > 0) {
	                    // choose t in s
	                	//TODO BSL ProbDist Here
	                	int i = Tools.randInt(s.size());
	                    Element chosen = s.get(i);
	                    // AddEnv(x,t)s
	                    interpreter.addEnv(variable.getKey(), chosen);
	                }
	                else {
	                	none = true;
	                	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                }
	            }
	            else {
	                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
	                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
	                return pos;
	            }
    		}
    		if (none) {
    			// RemoveEnv(x)
        		for (String x : variableMap.keySet())
        			interpreter.removeEnv(x);
    			// pos := delta
                return chooseNode.getIfnonePolicy();
    		}
    		 // pos := gamma
            return chooseNode.getDoPolicy();
    	}

    	// if policy 'P1' is evaluated 
    	else if (chooseNode.getDoPolicy().isEvaluated()) {
            // RemoveEnv(x)
    		for (String x : variableMap.keySet())
    			interpreter.removeEnv(x);
            // [pos] := (undef,u,undef)
            pos.setNode(null,null, chooseNode.getDoPolicy().getTriggers(), null);
            return pos;
    	}
    	
    	// if policy 'P2' is evaluated
    	else {
            // [pos] := (undef,u,undef)
            pos.setNode(null, null, chooseNode.getIfnonePolicy().getTriggers(), null);
            return pos;
    	}
	}


	/*
     * Interpreting policy of the form: 'choose x in E with C do P'
     */
	private ASTNode interpretChoosePolicy_WithCondition_NoIfnone(Interpreter interpreter, ASTNode pos) {
        ChoosePolicyNode chooseNode = (ChoosePolicyNode) pos;
        Map<Node, List<Element>> remained = getRemainedMap();
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated()) {
        		// considered(beta) := {}
        		remained.remove(domain);
        		// pos := beta
        		return domain;
        	}
        }
        
    	// if condition 'C' is not evaluated
    	if (!chooseNode.getCondition().isEvaluated())
    		return chooseVariableValues_WithCondition(chooseNode, remained, variableMap, interpreter);

    	// if domain 'E' is evaluated, condition 'C' is evaluated, but policy 'P' is not evaluated
    	else if (!chooseNode.getDoPolicy().isEvaluated()) {
            boolean value = false;            
            if (chooseNode.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) chooseNode.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of choose condition is not Boolean.", chooseNode.getCondition(), interpreter);
                return pos;
            }
            
            if (value) {
                // pos := delta
                return chooseNode.getDoPolicy();
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(chooseNode.getCondition());
                
                return chooseNode;
            }
    	}
        
    	// if domain 'E' is evaluated, condition 'C' is evaluated, and policy 'P' is evaluated
    	else {
            // RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,null,chooseNode.getDoPolicy().getTriggers(),null);
            return pos;
    	}
	}


	/*
     * Interpreting policy of the form: 'choose x in E with C do P1 ifnone P2'
     */
    private ASTNode interpretChoosePolicy_WithCondition_WithIfnone(Interpreter interpreter, ASTNode pos) {
        ChoosePolicyNode chooseNode = (ChoosePolicyNode) pos;
        Map<Node, List<Element>> remained = getRemainedMap();
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated()) {
        		// considered(beta) := {}
        		remained.remove(domain);
        		// pos := beta
        		return domain;
        	}
        }

    	// if condition 'C' is not evaluated
    	if (!chooseNode.getCondition().isEvaluated() && !chooseNode.getIfnonePolicy().isEvaluated())
    		return chooseVariableValues_WithCondition(chooseNode, remained, variableMap, interpreter);

    	// if domain 'E' is evaluated, condition 'C' is evaluated, but neither of the policies 'P1' or 'P2' are evaluated
    	else if (chooseNode.getCondition().isEvaluated() && !chooseNode.getDoPolicy().isEvaluated() && !chooseNode.getIfnonePolicy().isEvaluated()) {
            boolean value = false;            
            if (chooseNode.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) chooseNode.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of choose condition not Boolean", chooseNode.getCondition(), interpreter);
                return pos;
            }
            
            if (value) {
                // pos := delta
                return chooseNode.getDoPolicy();
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(chooseNode.getCondition());
                
                return chooseNode;
            }
    	}
        
    	// if domain 'E' is evaluated, condition 'C' is evaluated, and policy 'P1' is evaluated
    	else if (chooseNode.getCondition().isEvaluated() && chooseNode.getDoPolicy().isEvaluated()) {
            // RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,null,chooseNode.getDoPolicy().getTriggers(), null);
            return pos;
    	}

    	// if domain 'E' is evaluated and policy 'P2' is evaluated
    	else if (chooseNode.getIfnonePolicy().isEvaluated()) {
    		// RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,null,chooseNode.getIfnonePolicy().getTriggers(),null);
            return pos;
    	}
        
        // in case of error
        return pos;
	}
    
    private ASTNode chooseVariableValues_WithCondition(ChoosePolicyNode chooseNode, Map<Node, List<Element>> remained, Map<String, ASTNode> variableMap, Interpreter interpreter) {
    	// pos := gamma
    	ASTNode pos = chooseNode.getCondition();
    	boolean shouldChoose = true;
		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    		if (variable.getValue().getValue() instanceof Enumerable) {
                // s := enumerate(v)/considered(beta)
            	List<Element> s = remained.get(variable.getValue());
                if (s == null) {
        			Enumerable domain = (Enumerable) variable.getValue().getValue();
        			if (domain.supportsIndexedView())
        				s = new ArrayList<Element>(domain.getIndexedView());
        			else 
        				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
                	if (s.isEmpty()) {
                		if (chooseNode.getIfnonePolicy() == null) {
                			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
            	    			if (remained.remove(var.getValue()) != null)
            	    				interpreter.removeEnv(var.getKey());
            	    		}
            				// [pos] := (undef,{},undef)
                			chooseNode.setNode(null, null, new TriggerMultiset(), null);
            	            return chooseNode;
            			}
                		// pos := delta
                        pos = chooseNode.getIfnonePolicy();
                        interpreter.addEnv(variable.getKey(), Element.UNDEF);
                	}
                	remained.put(variable.getValue(), s);
                	shouldChoose = true;
                }
                else if (shouldChoose)
                	interpreter.removeEnv(variable.getKey());
                if (shouldChoose) {
	                if (!s.isEmpty()) {
	                    // choose t in s
	                	//TODO BSL ProbDIST HERE
	                    Element chosen = s.remove(Tools.randInt(s.size()));
	                    // AddEnv(x,t)s
	                    interpreter.addEnv(variable.getKey(), chosen);
	            	}
	            	else {
	            		remained.remove(variable.getValue());
	            		if (pos != chooseNode.getIfnonePolicy())
	            			pos = chooseNode;
	            		shouldChoose = true;
	            		continue;
	            	}
                }
            }
            else {
                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
                return pos;
            }
    		shouldChoose = false;
		}
		if (shouldChoose) {
			if (chooseNode.getIfnonePolicy() == null) {
				// [pos] := (undef,{},undef)
				chooseNode.setNode(null, null, new TriggerMultiset(), null);
	            return chooseNode;
			}
			// pos := delta
            pos = chooseNode.getIfnonePolicy();
		}
		if (pos == chooseNode.getIfnonePolicy()) {
			// RemoveEnv(x)
			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
    			if (remained.remove(var.getValue()) != null)
    				interpreter.removeEnv(var.getKey());
    		}
		}
        return pos;
    }

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	/**
	 * Mapping of node elements into the the choose policy node.
	 *   
	 * @author Roozbeh Farahbod
	 */
	public static class ChooseParseMap extends ParserTools.ArrayParseMap {

	    String nextChildName = "alpha";

	    public ChooseParseMap() {
			super(PLUGIN_NAME);
		}
		
		public Node map(Object[] v) {
			nextChildName = "alpha";
			ASTNode node = new ChoosePolicyNode(((Node)v[0]).getScannerInfo());

			addChildren(node, v);
			return node;
		}
		
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode)
				parent.addChild(nextChildName, child);
			else {
				String token = child.getToken();
		        if (token.equals("with"))
		        	nextChildName = ChoosePolicyPlugin.GUARD_NAME;
		        else if (token.equals("do"))
		        	nextChildName = ChoosePolicyPlugin.DO_POLICY_NAME;
		        else if (token.equals("ifnone"))
		        	nextChildName = ChoosePolicyPlugin.IFNONE_POLICY_NAME;
				parent.addChild(child);
		        //super.addChild(parent, child);
			}
		}

	}
}
