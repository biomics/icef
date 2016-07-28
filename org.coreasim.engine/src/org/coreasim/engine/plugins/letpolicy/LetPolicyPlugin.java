/*	
 * LetRulePlugin.java 	1.0 	$Revision: 243 $
 * 
 *
 * Copyright (C) 2006 George Ma
 * 
 * Last modified on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $ by $Author: rfarahbod $
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.letpolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.letpolicy.CompilerLetPolicyPlugin;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.AbstractStorage;
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
 *	Plugin for let policy
 *   
 *  @author  Eric Rothstein
 *  
 */
public class LetPolicyPlugin extends Plugin implements ParserPlugin, InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 9, 1, "");
	   
	public static final String PLUGIN_NAME = LetPolicyPlugin.class.getSimpleName();
	
	private Map<String, GrammarRule> parsers = null;

	private final String[] keywords = {"let", "in"};
	private final String[] operators = {"=", ",", "{", "[", "]", "}"};
	
	private final CompilerPlugin compilerPlugin = new CompilerLetPolicyPlugin(this);
	
	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public String[] getOperators() {
		return operators;
	}

	/* (non-Javadoc)
     * @see org.coreasm.engine.Plugin#interpret(org.coreasm.engine.interpreter.Node)
     */
    public ASTNode interpret(Interpreter interpreter, ASTNode pos) {
        if (pos instanceof LetPolicyNode) {
           LetPolicyNode letNode = (LetPolicyNode) (pos);
           Map<String, ASTNode> variableMap = null;
           AbstractStorage storage = capi.getStorage();

           try {
               variableMap = letNode.getVariableMap();
           } 
           catch (Exception e) {
               capi.error(e.getMessage(), pos, interpreter);
               return pos;
           }
           
           // evaluate all the terms that will be aliased
        	  for (ASTNode n :variableMap.values()) {
                   if (!n.isEvaluated())
                       return n;
               }
           
           if (!letNode.getInPolicy().isEvaluated()) {
//        	   clearLetPolicyResultChildNodes(letNode);
//        	   TriggerMultiset triggers = new TriggerMultiset();
               for (String v: variableMap.keySet()) {
//            	   //Compose the triggers
//            	   triggers.addAll(variableMap.get(v).getTriggers());
                   interpreter.addEnv(v,variableMap.get(v).getValue());
               }
               return letNode.getInPolicy();
           }
           else {
        	   TriggerMultiset composed = new TriggerMultiset();
               for (String v: variableMap.keySet()) {
            	   composed.addAll(variableMap.get(v).getTriggers());
                   interpreter.removeEnv(v);
               }
     
               composed.addAll(letNode.getInPolicy().getTriggers());
               storage.popState();
               pos.setNode(null,null,composed, null);
               return pos;
           }
        }        
        return pos;
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

    public Map<String, GrammarRule> getParsers() {
    	if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			KernelServices kernel = (KernelServices) capi.getPlugin("Kernel")
					.getPluginInterface();

			Parser<Node> policyParser = kernel.getPolicyParser();
			Parser<Node> termParser = kernel.getTermParser();

			ParserTools pTools = ParserTools.getInstance(capi);
			Parser<Node> idParser = pTools.getIdParser();
			
			Parser<Object[]> letTermParser = pTools.csplus(pTools.seq(
					idParser,
					pTools.getOprParser("="),
					termParser
					));
			

			Parser<Node> letPolicyParser = Parsers.array(
					new Parser[] {
					pTools.getKeywParser("let", PLUGIN_NAME),
					Parsers.or(	pTools.seq(pTools.getOprParser("{"), letTermParser, pTools.getOprParser("}")),
								pTools.seq(pTools.getOprParser("["), letTermParser, pTools.getOprParser("]")),
								letTermParser),
					pTools.getKeywParser("in", PLUGIN_NAME),
					policyParser
					}).map(
					new LetRuleParseMap());
			
			parsers.put("Policy",	
					new GrammarRule("LetPolicy", 
							"'let' ID '='  Term (',' ID '=' Term )* 'in' Policy", 
							letPolicyParser, PLUGIN_NAME));
    	}
    	
    	return parsers;
    }
    
    
    /* (non-Javadoc)
     * @see org.coreasm.engine.Plugin#initialize()
     */
    @Override
    public void initialize() {
       
    }

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	
	public static class LetRuleParseMap //extends ParseMapN<Node> {
	extends ParserTools.ArrayParseMap {
	
		public LetRuleParseMap() {
			super(PLUGIN_NAME);
		}

		String nextChildName = "alpha";
		
		public Node map(Object[] vals) {
			nextChildName = "alpha";
			LetPolicyNode node = new LetPolicyNode(((Node)vals[0]).getScannerInfo());
			if (vals[1] instanceof Object[] && ((Object[])vals[1])[0] instanceof Node) {
				Node n = ((Node)((Object[])vals[1])[0]);
				if ("[".equals(n.getToken()))
					addLetChildren(node, unpackChildren(new ArrayList<Node>(), vals));
				else
					addChildren(node, vals);
			}
			else
				addChildren(node, vals);
			return node;
		}
		
		private List<Node> unpackChildren(List<Node> nodes, Object[] vals) {
			for (Object child: vals) {
				if (child != null) {
					if (child instanceof Object[])
						unpackChildren(nodes, (Object[])child);
					else
						if (child instanceof Node)
							nodes.add((Node)child);
				}
			}
			return nodes;
		}
		
		private void addLetChildren(LetPolicyNode root, List<Node> children) {
			for (Node child: children) {
				if (child instanceof ASTNode) {
					if (!"alpha".equals(nextChildName) || root.getFirst() == null)
						addChild(root, child);
					else {
						LetPolicyNode newRoot = new LetPolicyNode(child.getScannerInfo());
						addChild(newRoot, child);
						nextChildName = "gamma";
						addChild(root, newRoot);
						root = newRoot;
					}
				} else
					addChild(root, child);
			}
		}

		@Override
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode) {
				parent.addChild(nextChildName, child);
			} else {
				parent.addChild(child);
				if (child.getToken().equals("="))				// Term
					nextChildName = "beta";
				else
					if (child.getToken().equals(","))			// ID
						nextChildName = "alpha";
					else
						if (child.getToken().equals("in"))		// Rule
							nextChildName = "gamma";
			}
		}
		
	}
}
