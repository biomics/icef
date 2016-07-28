/*	
 * ForallPolicyPlugin.java 	1.5 	$Revision: 243 $
 * 
 * Copyright (C) 2006 George Ma
 * Copyright (c) 2007 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.forallpolicy;

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
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.forall.CompilerForallPolicyPlugin;
import org.coreasim.engine.CoreASMError;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.TriggerMultiset;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.InterpreterException;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.util.Tools;

/** 
 *	Plugin for forall policy
 *   
 *  @author  Eric Rothstein, George Ma, Roozbeh Farahbod, Michael Stegmaier
 *  
 */
public class ForallPolicyPlugin extends Plugin implements ParserPlugin,
        InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 9, 3, "");
	
	public static final String PLUGIN_NAME = ForallPolicyPlugin.class.getSimpleName();
	
	private final String[] keywords = {"forall", "in", "with", "do", "ifnone", "endforall"};
	private final String[] operators = {};
	
    private ThreadLocal<Map<Node,List<Element>>> remained;
    private ThreadLocal<Map<Node,TriggerMultiset>> triggers;
    
    private Map<String, GrammarRule> parsers;
    
    private final CompilerPlugin compilerPlugin = new CompilerForallPolicyPlugin(this);
    
    @Override
    public CompilerPlugin getCompilerPlugin(){
    	return compilerPlugin;
    }
    
    @Override
    public void initialize() {
        //considered = new IdentityHashMap<Node,ArrayList<Element>>();
        remained = new ThreadLocal<Map<Node, List<Element>>>() {
			@Override
			protected Map<Node, List<Element>> initialValue() {
				return new IdentityHashMap<Node, List<Element>>();
			}
        };
        triggers= new ThreadLocal<Map<Node,TriggerMultiset>>() {
			@Override
			protected Map<Node, TriggerMultiset> initialValue() {
				return new IdentityHashMap<Node, TriggerMultiset>(); 
			}
        };
    }
 
    private Map<Node, List<Element>> getRemainedMap() {
    	return remained.get();
    }

    private Map<Node, TriggerMultiset> getTriggersMap() {
    	return triggers.get();
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
			
			ParserTools pTools = ParserTools.getInstance(capi);
			Parser<Node> idParser = pTools.getIdParser();
			
			Parser<Node> forallParser = Parsers.array( new Parser[] {
					pTools.getKeywParser("forall", PLUGIN_NAME),
					pTools.csplus(Parsers.array(idParser,
						pTools.getKeywParser("in", PLUGIN_NAME),
						termParser)),
					pTools.seq(
						pTools.getKeywParser("with", PLUGIN_NAME),
						guardParser).optional(),
					pTools.getKeywParser("do", PLUGIN_NAME),
					policyParser,
					pTools.seq(
						pTools.getKeywParser("ifnone", PLUGIN_NAME),
						policyParser).optional(),
					pTools.getKeywParser("endforall", PLUGIN_NAME).optional()
					}).map(
					new ForallParseMap());
			parsers.put("Policy", 
					new GrammarRule("ForallPolicy", 
							"'forall' ID 'in' Term (',' ID 'in' Term) ('with' Guard)? 'do' Policy ('ifnone' Policy)? ('endforall')?", forallParser, PLUGIN_NAME));
		}
		return parsers;
    }

    public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
        
        if (pos instanceof ForallPolicyNode) {
            ForallPolicyNode forallNode = (ForallPolicyNode) pos;
            Map<Node, List<Element>> remained = getRemainedMap();
            Map<Node, TriggerMultiset> triggers = getTriggersMap();
            Map<String, ASTNode> variableMap = null;
            
            try {
            	variableMap = forallNode.getVariableMap();
            }
            catch (CoreASMError e) {
            	capi.error(e);
            	return pos;
            }
            
            // evaluate all domains
            for (ASTNode domain : variableMap.values()) {
            	if (!domain.isEvaluated()) {
            		// SPEC: considered := {}
                	remained.remove(domain);
                    
                    // SPEC: pos := beta
            		return domain;
            	}
            }
            
            if (!forallNode.getDoPolicy().isEvaluated() &&
            		(forallNode.getIfnonePolicy() == null || !forallNode.getIfnonePolicy().isEvaluated()) &&
                    // depending on short circuit evaluation
                     ((forallNode.getCondition() == null) || !forallNode.getCondition().isEvaluated())) {
            	// pos := gamma
            	if (forallNode.getCondition() != null)
            		pos = forallNode.getCondition();
            	else
            		pos = forallNode.getDoPolicy();
            	boolean shouldChoose = true;
            	for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	                if (variable.getValue().getValue() instanceof Enumerable) {
	                    
	                    // SPEC: s := enumerate(v)/considered                    
	                    // ArrayList<Element> s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                    // s.removeAll(considered.get(variable.getValue()));
	                	// 
	                	// changed to the following to improve performance:
	        			List<Element> s = remained.get(variable.getValue());
	                	if (s == null) {
	            			Enumerable domain = (Enumerable)variable.getValue().getValue();
	            			if (domain.supportsIndexedView())
	            				s = new ArrayList<Element>(domain.getIndexedView());
	            			else
	            				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	            			if (s.isEmpty()) {
	            				if (forallNode.getIfnonePolicy() == null) {
	                    			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
	                	    			if (remained.remove(var.getValue()) != null)
	                	    				interpreter.removeEnv(var.getKey());
	                	    		}
	                				// [pos] := (undef,{},undef)
	                    			forallNode.setNode(null, new UpdateMultiset(), null, null);
	                	            return forallNode;
	            				}
                	         	// pos := delta
	                           	pos = forallNode.getIfnonePolicy();
	                           	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                    	}
	            			remained.put(variable.getValue(), s);
	                		shouldChoose = true;
	                	}
	                	else if (shouldChoose)
	                		interpreter.removeEnv(variable.getKey());
	                	
	                	if (shouldChoose) {
		                    if (!s.isEmpty()) {
		                    	// SPEC: considered := considered union {t}
		                        // choose t in s, for simplicty choose the first 
		                        // since we have to go through all of them
		                        Element chosen = s.remove(0);
		                        shouldChoose = false;
		                        
		                        // SPEC: AddEnv(x,t)
		                        interpreter.addEnv(variable.getKey(),chosen);
		                    }   
		                    else {
		                        remained.remove(variable.getValue());
		                        if (pos != forallNode.getIfnonePolicy())
			            			pos = forallNode;
		                    }
	                	}
	                }
	                else {
	                    capi.error("Cannot perform a 'forall' over " + Tools.sizeLimit(variable.getValue().getValue().denotation())
	                    		+ ". Forall domain must be an enumerable element.", variable.getValue(), interpreter);
	                    return pos;
	                }
            	}
            	if (shouldChoose) {
        			if (forallNode.getIfnonePolicy() == null || triggers.containsKey(forallNode)) {
            			// we're done
        				TriggerMultiset triggerSet = triggers.remove(pos);
        				if (triggerSet == null)
        					triggerSet = new TriggerMultiset();
        				forallNode.setNode(null, null, triggerSet, null);
        	            return forallNode;
        			}
        			// pos := delta
        			pos = forallNode.getIfnonePolicy();
        		}
            }
            else if (((forallNode.getCondition() != null) && forallNode.getCondition().isEvaluated()) &&
                     !forallNode.getDoPolicy().isEvaluated() &&
                     (forallNode.getIfnonePolicy() == null || !forallNode.getIfnonePolicy().isEvaluated())) {
                
                boolean value = false;            
                if (forallNode.getCondition().getValue() instanceof BooleanElement) {
                    value = ((BooleanElement) forallNode.getCondition().getValue()).getValue();
                }
                else {
                    capi.error("Value of forall condition is not Boolean.", forallNode.getCondition(), interpreter);
                    return pos;
                }
                
                if (value) {
                    // pos := delta
                    return forallNode.getDoPolicy();
                }
                else {
                    // ClearTree(gamma)
                    interpreter.clearTree(forallNode.getCondition());
                    
                    // pos := beta
                    return forallNode;
                }
                
            }
            else if (((forallNode.getCondition() == null) || forallNode.getCondition().isEvaluated()) && 
                    (forallNode.getDoPolicy().isEvaluated())) {    
                
            	TriggerMultiset triggerSet = triggers.get(pos);
            	if (triggerSet == null) {
	            	// SPEC: [pos] := {undef,{},undef}
            		triggerSet = new TriggerMultiset();
            		triggers.put(pos,triggerSet);
            	}
                // [pos] := (undef,updates(pos) union u,undef)                
                if (forallNode.getDoPolicy().getTriggers() != null)
                	triggerSet.addAll(forallNode.getDoPolicy().getTriggers());
                
                // ClearTree(gamma/delta)
                interpreter.clearTree(forallNode.getDoPolicy());
                
                if (forallNode.getCondition() != null) {
                    // ClearTree(gamma)
                    interpreter.clearTree(forallNode.getCondition());
                }
                
                return pos;
            }
            else if (forallNode.getIfnonePolicy() != null && forallNode.getIfnonePolicy().isEvaluated()) {
                // [pos] := (undef,u,undef)
                pos.setNode(null,null, forallNode.getIfnonePolicy().getTriggers(),null);
                return pos;
            }
            if (pos == forallNode.getIfnonePolicy()) {
            	// RemoveEnv(x)
        		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
        			if (remained.remove(variable.getValue()) != null)
        				interpreter.removeEnv(variable.getKey());
        		}
            }
        }
        
        return pos;
    }

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	public static class ForallParseMap //extends ParseMapN<Node> {
	extends ParserTools.ArrayParseMap {

		String nextChildName = "alpha";
		
		public ForallParseMap() {
			super(PLUGIN_NAME);
		}

		public Node map(Object[] vals) {
			nextChildName = "alpha";
            Node node = new ForallPolicyNode(((Node)vals[0]).getScannerInfo());
            addChildren(node, vals);
			return node;
		}

		@Override
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode)
				parent.addChild(nextChildName, child);
			else {
				String token = child.getToken();
		        if (token.equals("with"))
		        	nextChildName = "guard";
		        else if (token.equals("do"))
	        		nextChildName = "policy";
		        else if (token.equals("ifnone"))
		        	nextChildName = "ifnone";
				super.addChild(parent, child);
			}
		}
		
	}

	/**
	 * @return <code>null</code>
	 */
	public Parser<Node> getParser(String nonterminal) {
		return null;
	}

	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}
	
}
