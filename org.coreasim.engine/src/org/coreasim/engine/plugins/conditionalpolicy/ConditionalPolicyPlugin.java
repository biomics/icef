/*
 * ConditionalPolicyPlugin.java 1.0 
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */

package org.coreasim.engine.plugins.conditionalpolicy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.conditionalpolicy.CompilerConditionalPolicyPlugin;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.TriggerMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.parser.ParserTools.ArrayParseMap;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;

/**
 * Plugin for conditional policy
 * 
 * @author Eric Rothstein
 * 
 */
public class ConditionalPolicyPlugin extends Plugin
		implements ParserPlugin, InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 9, 1, "");

	public static final String PLUGIN_NAME = ConditionalPolicyPlugin.class.getSimpleName();

	private final String[] keywords = { "if", "then", "else", "endif" };
	private final String[] operators = {};

	private Map<String, GrammarRule> parsers = null;

	private final CompilerPlugin compilerPlugin = new CompilerConditionalPolicyPlugin(this);
	
	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}
	
	@Override
	public String[] getKeywords() {
		return keywords;
	}

	@Override
	public String[] getOperators() {
		return operators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.coreasm.engine.Plugin#interpret(org.coreasm.engine.interpreter.Node)
	 */
	@Override
	public ASTNode interpret(Interpreter interpreter, ASTNode pos) {

		if (pos instanceof ConditionalPolicyNode) {
			ConditionalPolicyNode conditionalNode = (ConditionalPolicyNode) pos;
			
			if (!conditionalNode.getGuard().isEvaluated()) {
				return conditionalNode.getGuard();
			}
			else {
				if (!(conditionalNode.getGuard().getValue() instanceof BooleanElement))
					capi.error("Element used as guard within \"if\" is not a boolean.",
							conditionalNode.getGuard(),
							interpreter);
				if (conditionalNode.getGuard().getValue().equals(BooleanElement.TRUE)) {
					if (!conditionalNode.getIfPolicy().isEvaluated()) {
						return conditionalNode.getIfPolicy();
					}
					else {
						pos.setNode(null,null, conditionalNode.getIfPolicy().getTriggers(), null);
						return pos;
					}
				}
				else { // guard is false
					if (conditionalNode.getElsePolicy() == null) { // there is no else 
						pos.setNode(null, null, new TriggerMultiset(), null);
						return pos;
					}
					else {
						if (!conditionalNode.getElsePolicy().isEvaluated()) {
							return conditionalNode.getElsePolicy();
						}
						else {
							pos.setNode(null,null, conditionalNode.getElsePolicy().getTriggers(), null);
							return pos;
						}
					}
				}
			}
		}
		else if (pos instanceof ConditionalTermNode) {
			ConditionalTermNode conditionalTerm = (ConditionalTermNode)pos;
			if (!conditionalTerm.getCondition().isEvaluated())
				return conditionalTerm.getCondition();
			if (!(conditionalTerm.getCondition().getValue() instanceof BooleanElement))
				throw new CoreASIMError("The value of the condition of a conditional term must be a BooleanElement but was " + conditionalTerm.getCondition().getValue() + ".", conditionalTerm.getCondition());
			BooleanElement value = (BooleanElement)conditionalTerm.getCondition().getValue();
			if (value.getValue()) {
				if (!conditionalTerm.getIfTerm().isEvaluated())
					return conditionalTerm.getIfTerm();
				pos.setNode(null, null, new TriggerMultiset(),conditionalTerm.getIfTerm().getValue());
			}
			else {
				if (!conditionalTerm.getElseTerm().isEvaluated())
					return conditionalTerm.getElseTerm();
				pos.setNode(null, null, new TriggerMultiset(), conditionalTerm.getElseTerm().getValue());
			}
		}
		else {
			return null;
		}
		return pos;
	}

	@Override
	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}

	/**
	 * @return <code>null</code>
	 */
	@Override
	public Parser<Node> getParser(String nonterminal) {
		return null;
	}

	@Override
	public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			KernelServices kernel = (KernelServices) capi.getPlugin("Kernel").getPluginInterface();

			Parser<Node> policyParser = kernel.getPolicyParser();
			Parser<Node> guardParser = kernel.getGuardParser();
			Parser<Node> termParser = kernel.getTermParser();

			ParserTools pTools = ParserTools.getInstance(capi);

			Parser<Node> condPolicyParser = Parsers.array(
					new Parser[] {
							pTools.getKeywParser("if", PLUGIN_NAME),
							guardParser,
							pTools.getKeywParser("then", PLUGIN_NAME),
							policyParser,
							Parsers.array(
									pTools.getKeywParser("else", PLUGIN_NAME),
									policyParser).optional(),
							pTools.getKeywParser("endif", PLUGIN_NAME).optional()
					}).map(new ConditionalParseMap());
			parsers.put("Policy",
					new GrammarRule("ConditionalPolicy",
							"'if' Guard 'then' Policy ('else' Policy )? ('endif')?",
							condPolicyParser, PLUGIN_NAME));
			
			Parser<Node> condTermParser = Parsers.array(pTools.getKeywParser("if", PLUGIN_NAME),
					termParser,
					pTools.getKeywParser("then", PLUGIN_NAME),
					termParser,
					pTools.getKeywParser("else", PLUGIN_NAME),
					termParser).map(new ArrayParseMap(PLUGIN_NAME) {
				public Node map(Object[] vals) {
					Node node = new ConditionalTermNode(((Node)vals[0]).getScannerInfo());
					addChildren(node, vals);
					return node;
				}
			});
			parsers.put("BasicTerm", new GrammarRule("ConditionalTerm", "'if' Term 'then' Term 'else' Term",
					condTermParser, PLUGIN_NAME));

		}
		return parsers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.coreasm.engine.Plugin#initialize()
	 */
	@Override
	public void initialize() {

	}

	@Override
	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	public static class ConditionalParseMap extends ArrayParseMap {

		String nextChildName = "guard";

		public ConditionalParseMap() {
			super(PLUGIN_NAME);
		}

		@Override
		public Node map(Object[] vals) {
			nextChildName = "guard";
			Node node = new ConditionalPolicyNode(((Node) vals[0]).getScannerInfo());
			addChildren(node, vals);
			return node;
		}

		@Override
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode)
				parent.addChild(nextChildName, child);
			else {
				String token = child.getToken();
				if (token.equals("then"))
					nextChildName = "policy";
				//super.addChild(parent, child);
				parent.addChild(child);
			}
		}

	}
}
