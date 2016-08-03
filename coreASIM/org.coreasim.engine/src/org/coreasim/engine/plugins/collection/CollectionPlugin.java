/*
 * CollectionPlugin.java 		1.0
 * 
 * Copyright (c) 2007 Roozbeh Farahbod
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
 */

package org.coreasim.engine.plugins.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.collection.CompilerCollectionPlugin;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.PolicyElement;
import org.coreasim.engine.absstorage.RuleElement;
import org.coreasim.engine.absstorage.UniverseElement;
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
import org.coreasim.engine.plugin.VocabularyExtender;
import org.coreasim.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base plugin of all the collection plugins
 *   
 * @author Roozbeh Farahbod
 * 
 */

public class CollectionPlugin extends Plugin 
	implements ParserPlugin, InterpreterPlugin, VocabularyExtender {

	protected static final Logger logger = LoggerFactory.getLogger(CollectionPlugin.class);

	/** plugin name */
	public static final String PLUGIN_NAME = CollectionPlugin.class.getSimpleName();
	
	/** version info */
	public static final VersionInfo version = new VersionInfo(0, 1, 1, "beta");
	
	private HashMap<String, GrammarRule> parsers = null;
	private Map<String, FunctionElement> functions = null;
	private Set<String> dependencyNames = null;
	
	private final String[] keywords = {"add", "to", "remove", "from"};
	private final String[] operators = {};
	
	private final CompilerPlugin compilerPlugin = new CompilerCollectionPlugin(this);
	
	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {
	}

	@Override
	public Set<String> getDependencyNames() {
		if (dependencyNames == null) {
			dependencyNames = new HashSet<String>();
			dependencyNames.add("NumberPlugin");
		}
		return dependencyNames;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.VersionInfoProvider#getVersionInfo()
	 */
	public VersionInfo getVersionInfo() {
		return version;
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
			KernelServices kernel = (KernelServices) capi.getPlugin("Kernel")
					.getPluginInterface();

			Parser<Node> termParser = kernel.getTermParser();

			ParserTools pTools = ParserTools.getInstance(capi);
			Parser<Node> addtoRuleParser = Parsers.array(
					new Parser[] {
						pTools.getKeywParser("add", PLUGIN_NAME),
						termParser,
						pTools.getKeywParser("to", PLUGIN_NAME),
						termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						public Node map(Object[] vals) {
							Node node = new AddToRuleNode(((Node)vals[0]).getScannerInfo());
							addChildren(node, vals);
							return node;
						}
			});
			parsers.put("AddToCollectionRule",
					new GrammarRule("AddToCollectionRule", "'add' Term 'to' Term",
							addtoRuleParser, PLUGIN_NAME));
			
			//
			Parser<Node> removefromRuleParser = Parsers.array(
					new Parser[] {
						pTools.getKeywParser("remove", PLUGIN_NAME),
						termParser,
						pTools.getKeywParser("from", PLUGIN_NAME),
						termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						public Node map(Object[] vals) {
							Node node = new RemoveFromRuleNode(((Node)vals[0]).getScannerInfo());
							addChildren(node, vals);
							return node;
						}
			});
			parsers.put("RemoveFromCollectionRule",
					new GrammarRule("RemoveFromCollectionRule", "'remove' Term 'from' Term",
							removefromRuleParser, PLUGIN_NAME));

			// Rule : AddToCollectionRule | RemoveFromCollectionRule
			parsers.put("Rule",
					new GrammarRule("SetRule", "AddToCollectionRule | RemoveFromCollectionRule",
							Parsers.or(addtoRuleParser, removefromRuleParser), PLUGIN_NAME));

		}
		
		return parsers;

	}

	public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
		ASTNode nextPos = pos;
		String gClass = pos.getGrammarClass();
        
		// if collection related rule
		//TODO BSL maybe needs to have POLICY_CLASS case?
		if (gClass.equals(ASTNode.RULE_CLASS))
		{
			// add/to rule
			if (pos instanceof AddToRuleNode) {
				// add/to rule wrapper wrapper
				AddToRuleNode atNode = (AddToRuleNode)pos;
				ASTNode collectionNode = atNode.getToNode();
				
				nextPos = atNode.getUnevaluatedTerm();
				
				// no unevaluated terms
				if (nextPos == null)
				{
					// set next pos to current position
					nextPos = pos;
					
					if (atNode.getToLocation() != null) {
					
						if (collectionNode.getValue() instanceof ModifiableCollection) {

							try {
							// set vul for node
							pos.setNode(
									null, 
									((ModifiableCollection)collectionNode.getValue()).computeAddUpdate(
											atNode.getToLocation(),
											atNode.getAddElement(),
											interpreter.getSelf(),
											pos),
									null,		
									null);
							} catch (InterpreterException e) {
								capi.error(e.getMessage(), pos, interpreter);
							}

						} else
							capi.error("Incremental add update only applies to modifiable enumerables." + Tools.getEOL() 
									+ "Failed adding " + atNode.getAddElement() + " to " + collectionNode.getValue() + ".", 
									atNode, interpreter);
					} else
						capi.error("Cannot perform incremental add update on a non-location!", atNode, interpreter);
				}
				
			}
			// remove/from rule
			else if (pos instanceof RemoveFromRuleNode)
			{
				// remove/from rule wrapper wrapper
				RemoveFromRuleNode rfNode = (RemoveFromRuleNode)pos;
				ASTNode collectionNode = rfNode.getFromNode();
				
				nextPos = rfNode.getUnevaluatedTerm();
				
				// no unevaluated terms
				if (nextPos == null)
				{
					// set next pos to current position
					nextPos = pos;
				
					if (rfNode.getFromLocation() != null) {
						
						if (collectionNode.getValue() instanceof ModifiableCollection) {

							try{
								// set vul for node
								pos.setNode(
										null, 
										((ModifiableCollection)collectionNode.getValue()).computeRemoveUpdate(
												rfNode.getFromLocation(), 
												rfNode.getRemoveElement(), 
												interpreter.getSelf(),
												pos),
										null,
										null);
							} catch (InterpreterException e) {
								capi.error(e.getMessage(), pos, interpreter);
							}
							
						} else
							capi.error("Incremental remove update only applies to modifiable enumerables." + Tools.getEOL() 
										+ "Failed adding " + rfNode.getRemoveElement() + " to " + collectionNode.getValue() + ".", 
										rfNode, interpreter);
					} else
						capi.error("Cannot perform incremental remove update on a non-location!", rfNode, interpreter);
				}
			}
		}
            
        return nextPos;
	}

	public Set<String> getBackgroundNames() {
		return Collections.emptySet();
	}

	public Map<String, BackgroundElement> getBackgrounds() {
		return null;
	}

	public Set<String> getFunctionNames() {
		return getFunctions().keySet();
	}

	public Map<String, FunctionElement> getFunctions() {
		if (functions == null) {
			functions = new HashMap<String, FunctionElement>();
			
			// moved back to NumberPlugin
			//functions.put(SizeFunctionElement.NAME, new SizeFunctionElement());

			functions.put(MapFunctionElement.NAME, new MapFunctionElement(capi));
			functions.put(FilterFunctionElement.NAME, new FilterFunctionElement(capi));
			functions.put(FoldFunctionElement.FOLD_NAME, new FoldFunctionElement(capi, true));
			functions.put(FoldFunctionElement.FOLDR_NAME, new FoldFunctionElement(capi, true));
			functions.put(FoldFunctionElement.FOLDL_NAME, new FoldFunctionElement(capi, false));
		}
		return functions;
	}

	public Set<String> getRuleNames() {
		return Collections.emptySet();
	}

	public Map<String, RuleElement> getRules() {
		return null;
	}

	public Set<String> getUniverseNames() {
		return Collections.emptySet();
	}

	public Map<String, UniverseElement> getUniverses() {
		return null;
	}

	@Override
	public Map<String, PolicyElement> getPolicies() {
		return Collections.emptyMap();
	}

	@Override
	public Set<String> getPolicyNames() {
		return Collections.emptySet();
	}

}
