/*	
 * AbstractionPlugin.java  	1.0
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
 
package org.coreasim.engine.plugins.abstraction;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.abstraction.CompilerAbstractionPlugin;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.Update;
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
import org.coreasim.engine.plugins.io.IOPlugin;
import org.coreasim.engine.plugins.string.StringElement;

/** 
 * Abstraction plugin facilitates writing abstract specifications.
 *  
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class AbstractionPlugin extends Plugin 
		implements ParserPlugin, InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 1, 0, "");

	public static final String PLUGIN_NAME = AbstractionPlugin.class.getSimpleName();

	/** Name of the abstract info function */
	public static final String ABSTRACT_INFO_FUNC_NAME = "abstractInfo";
	
	/** Location of abstract info function */
	public static final Location ABSTRACT_INFO_FUNC_LOC = new Location(ABSTRACT_INFO_FUNC_NAME, ElementList.NO_ARGUMENT);

	/** Print Action */
	public static final String ABSTRACT_CALL_ACTION = "abstractCallAction";
	public static final String[] UPDATE_ACTIONS = {ABSTRACT_CALL_ACTION};

	private final Set<String> dependencyList;
	private HashMap<String, GrammarRule> parsers = null;
	
	private final String[] keywords = {"abstract"};
	private final String[] operators = {};
	
	private final CompilerPlugin compilerPlugin = new CompilerAbstractionPlugin(this);
	
	public AbstractionPlugin() {
		dependencyList = new HashSet<String>();
		dependencyList.add("StringPlugin");
		dependencyList.add("IOPlugin");
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {}

	@Override
	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.ParserPlugin#getParser(java.lang.String)
	 */
	@Override
	public Parser<Node> getParser(String nonterminal) {
		return null;
	}

	@Override
	public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers  = new HashMap<String, GrammarRule>();
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> termParser = kernel.getTermParser();
			
			ParserTools pTools = ParserTools.getInstance(capi);
			
			Parser<Node> abstractRuleParser = Parsers.array(
					new Parser[] {
					pTools.getKeywParser("abstract", PLUGIN_NAME),
					termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						@Override
						public Node map(Object[] vals) {
							Node node = new AbstractRuleNode(((Node)vals[0]).getScannerInfo());
							node.addChild((Node) vals[0]);
							node.addChild("alpha", (Node)vals[1]);
							return node;
						}
				
					});
			parsers.put("Rule", 
					new GrammarRule(abstractRuleParser.toString(),
							"'abstract' Term", abstractRuleParser, PLUGIN_NAME));
		}
		
		return parsers;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.VersionInfoProvider#getVersionInfo()
	 */
	@Override
	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#getDependencyNames()
	 */
	@Override
	public Set<String> getDependencyNames() {
		return this.dependencyList;
	}

	@Override
	public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
		if (pos instanceof AbstractRuleNode) 
			return interpretAbstractRule(interpreter, (AbstractRuleNode)pos);
		else
			return pos;
	}
	
	/*
	 * Interprets the Pritn rule.
	 */
	private ASTNode interpretAbstractRule(Interpreter interpreter, AbstractRuleNode pos) throws InterpreterException {
		if (!pos.getMessage().isEvaluated()) {
			return pos.getMessage();
		} else {
			pos.setNode(
					null, 
					new UpdateMultiset(
							new Update(
									IOPlugin.PRINT_OUTPUT_FUNC_LOC,
									new StringElement("Abstract Call: " + pos.getMessage().getValue().toString()),
									IOPlugin.PRINT_ACTION,
									interpreter.getSelf(),
									pos.getScannerInfo()
									)),
					null,
					null);
		}
		return pos;
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
	public void aggregateUpdates(PluginAggregationAPI pluginAgg) {
		// all locations on which contain print actions
		UpdateMultiset updatesToAggregate = pluginAgg.getLocUpdates(ABSTRACT_INFO_FUNC_LOC);
		
		//go over all these updates and print a message
	}

	public void compose(PluginCompositionAPI compAPI) {
		// First, add all the updates in the first set
		for (Update u: compAPI.getLocUpdates(1, ABSTRACT_INFO_FUNC_LOC)) {
			compAPI.addComposedUpdate(u, this);
		}
		// Second, add all the updates in the second set
		for (Update u: compAPI.getLocUpdates(2, ABSTRACT_INFO_FUNC_LOC)) {
			compAPI.addComposedUpdate(u, this);
		}
		
	}

	public String[] getUpdateActions() {
		return UPDATE_ACTIONS;
	}

	*/
	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}
}
