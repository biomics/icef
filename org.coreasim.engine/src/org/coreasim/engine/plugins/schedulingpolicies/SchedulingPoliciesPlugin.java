/*
 * SchedulingPoliciesPlugin.java 		1.0
 * 
 * Copyright (c) 2008 Roozbeh Farahbod
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


package org.coreasim.engine.plugins.schedulingpolicies;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.engine.EngineError;
import org.coreasim.engine.EngineException;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.CoreASIMEngine.EngineMode;
import org.coreasim.engine.absstorage.AbstractStorage;
import org.coreasim.engine.absstorage.AbstractUniverse;
import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.MapFunction;
import org.coreasim.engine.absstorage.NameElement;
import org.coreasim.engine.absstorage.PolicyElement;
import org.coreasim.engine.absstorage.RuleElement;
import org.coreasim.engine.absstorage.UniverseElement;
import org.coreasim.engine.absstorage.Update;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.InterpreterException;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParseMap;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugin.ExtensionPointPlugin;
import org.coreasim.engine.plugin.InitializationFailedException;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugin.SchedulerPlugin;
import org.coreasim.engine.plugin.VocabularyExtender;
import org.coreasim.engine.scheduler.SchedulingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides some basic scheduling policies for running agents.
 *   
 * @author Roozbeh Farahbod
 *
 */

public class SchedulingPoliciesPlugin extends Plugin implements 
			SchedulerPlugin, ParserPlugin, InterpreterPlugin, VocabularyExtender, ExtensionPointPlugin {

	protected static final Logger logger = LoggerFactory.getLogger(SchedulingPoliciesPlugin.class);

	public static final VersionInfo VERSION_INFO = new VersionInfo(1, 6, 3, "alpha");
	
	public static final String PLUGIN_NAME = SchedulingPoliciesPlugin.class.getSimpleName();
	
	/** The name of the SchedulingPolicies.Policy property */
	public static final String POLICY_PROPERTY = "SchedulingPolicies.policy";
	
	/** default value of {@link #POLICY_PROPERTY} property */ 
	public static final String DEFAULT_POLICY_NAME = "default";
	
	/** the policy that tries to run all the agents together in every step */
	public static final String ALL_FIRST_NAME = "allfirst";

	/** the policy that tries agents one by one */
	public static final String ONE_BY_ONE_NAME = "onebyone";

	/** the policy that tries agents one by one with no guarantee of fairness*/
	public static final String ONE_BY_ONE_UNFAIR_NAME = "onebyoneunfair";
	
	private static final String SUSPEND_AGENT_KEYWORD = "suspend";
	private static final String RESUME_AGENT_KEYWORD = "resume";
	private static final String TERMINATE_AGENT_KEYWORD = "terminate";
	private static final String SHUTDOWN_KEYWORD = "shutdown";
	private static final String AGENT_SCHEDULING_STATUS_FUNC_NAME = "SchedulingPolicies.agentSchedulingStatus";

	private final NameElement suspendedFlag = new NameElement("suspended");
	private final NameElement terminatedFlag = new NameElement("terminated");
	private final MapFunction agentSchedulingStatusFunction = new MapFunction();

	private final String[] keywords = {RESUME_AGENT_KEYWORD, SUSPEND_AGENT_KEYWORD, TERMINATE_AGENT_KEYWORD, SHUTDOWN_KEYWORD};
	private final String[] operators = {};
	private Map<EngineMode, Integer> targetModes = null;
	
	private SchedulingPolicy currentPolicy = null;
	
	private Set<Element> suspendedOrTerminatedAgents = new HashSet<Element>();

	private Map<String, GrammarRule> parsers = null;

	private Map<String, FunctionElement> funcs = null;
	
	@Override
	public void initialize() throws InitializationFailedException {
		agentSchedulingStatusFunction.clear();
	}

	public SchedulingPolicy getPolicy() {
		currentPolicy = createPolicy();
		return currentPolicy;
	}

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	/*
	 * Creates a new policy based on the value of 
	 * the POLICY_PROPERTY property. 
	 */
	private SchedulingPolicy createPolicy() {
		String policyName = capi.getProperty(POLICY_PROPERTY);
		if (policyName == null) 
			policyName = DEFAULT_POLICY_NAME;
		
		if (policyName.equals(DEFAULT_POLICY_NAME)) 
			return new BasicSchedulingPolicy(suspendedOrTerminatedAgents);
		else
			if (policyName.equals(ALL_FIRST_NAME))
				return new AllFirstSchedulingPolicy(suspendedOrTerminatedAgents);
			else
				if (policyName.equals(ONE_BY_ONE_NAME))
					return new OneByOneSchedulingPolicy(suspendedOrTerminatedAgents);
				else
					if (policyName.equals(ONE_BY_ONE_UNFAIR_NAME)) 
						return new OneByOneUnfairSchedulingPolicy(suspendedOrTerminatedAgents);
		
		throw new EngineError("Scheduling policy '" + policyName + "' not found.");
	}

	public String[] getKeywords() {
		return keywords;
	}

	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}

	public String[] getOperators() {
		return operators;
	}

	public Parser<Node> getParser(String nonterminal) {
		return null;
	}

	public Map<String, GrammarRule> getParsers() {
		if (parsers  == null) {
			parsers = new HashMap<String, GrammarRule>();
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> termParser = kernel.getTermParser();
			
			ParserTools pTools = ParserTools.getInstance(capi);
			
			// SuspendAgentRule ::= 'suspend' TERM
			Parser<Node> suspendParser = Parsers.array(
					new Parser[] {
					pTools.getKeywParser(SUSPEND_AGENT_KEYWORD, PLUGIN_NAME),
					termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						public Node map(Object[] vals) {
							Node node = new AgentManagementRuleNode(((Node)vals[0]).getScannerInfo(), "SuspendAgentRule");
							node.addChild((Node)vals[0]);
							node.addChild("alpha", (Node)vals[1]);
							return node;
						}
				
					});

			// ResumeAgentRule ::= 'resume' TERM
			Parser<Node> resumeParser = Parsers.array(
					new Parser[] {
					pTools.getKeywParser(RESUME_AGENT_KEYWORD, PLUGIN_NAME),
					termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						public Node map(Object[] vals) {
							Node node = new AgentManagementRuleNode(((Node)vals[0]).getScannerInfo(), "ResumeAgentRule");
							node.addChild((Node)vals[0]);
							node.addChild("alpha", (Node)vals[1]);
							return node;
						}
				
					});

			// TerminateAgentRule ::= 'terminate' TERM
			Parser<Node> terminateParser = Parsers.array(
					new Parser[] {
					pTools.getKeywParser(TERMINATE_AGENT_KEYWORD, PLUGIN_NAME),
					termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						public Node map(Object[] vals) {
							Node node = new AgentManagementRuleNode(((Node)vals[0]).getScannerInfo(), "TerminateAgentRule");
							node.addChild((Node)vals[0]);
							node.addChild("alpha", (Node)vals[1]);
							return node;
						}
				
					});

			// ShutdownRule ::= 'shutdown'
			Parser<Node> shutdownParser = 
					pTools.getKeywParser(SHUTDOWN_KEYWORD, PLUGIN_NAME).map(
					new ParseMap<Node, Node>(PLUGIN_NAME) {

						public Node map(Node v) {
							Node node = new AgentManagementRuleNode(v.getScannerInfo(), "ShutdownRule");
							node.addChild(v);
							return node;
						}

					});

			// AgentManagementRule ::= SuspendAgentParserRule | ResumeAgentParserRule | TerminateAgentRule | ShutdownRule
			final String grbody = suspendParser.toString() + " | " 
								+ resumeParser.toString() + " | "
								+ terminateParser.toString() + " | "
								+ shutdownParser.toString();
			final GrammarRule gr = new GrammarRule("AgentManagementRule", grbody, 
						Parsers.or(suspendParser, resumeParser, terminateParser, shutdownParser), PLUGIN_NAME);
			
			parsers.put(gr.name, gr);
			
			parsers.put("Rule", new GrammarRule("Rule", gr.name, gr.parser, PLUGIN_NAME)); 
		}
		
		return parsers;
	}

	public ASTNode interpret(Interpreter interpreter, ASTNode pos)
			throws InterpreterException {
		
		if (pos instanceof AgentManagementRuleNode) {
			AgentManagementRuleNode node = (AgentManagementRuleNode)pos;
			
			if (node.getKeyword().equals(SUSPEND_AGENT_KEYWORD)) {
				ASTNode agentNode = node.getAgent();
				
				if (!agentNode.isEvaluated())
					return agentNode; 

				if (agentNode.getValue() == null) {
					capi.error("The agent argument passed to 'suspend' does not have a value.", pos, interpreter);
				} else {
					pos.setNode(null, new UpdateMultiset(
							new Update(getSchedulingStatusLocation(agentNode.getValue()), 
									suspendedFlag, Update.UPDATE_ACTION, interpreter.getSelf(), pos.getScannerInfo())),null, null);

					logger.debug("Suspending agent '{}'.", agentNode.getValue());
				}
			} else 
				if (node.getKeyword().equals(RESUME_AGENT_KEYWORD)) {
					ASTNode agentNode = node.getAgent();
					
					if (!agentNode.isEvaluated())
						return agentNode; 
					
					if (agentNode.getValue() == null) {
						capi.error("The agent argument passed to 'resume' does not have a value.", pos, interpreter);
					} else {
						if (agentSchedulingStatusFunction.getValue(new ElementList(agentNode.getValue())) == suspendedFlag) {
							
							pos.setNode(null, new UpdateMultiset(
									new Update(getSchedulingStatusLocation(agentNode.getValue()), 
											Element.UNDEF, Update.UPDATE_ACTION, interpreter.getSelf(), pos.getScannerInfo())), null, null);

							logger.debug("Resuming agent '{}'", agentNode.getValue());
						} else
							capi.error("The agent is not suspended and cannot be resumed.", pos, interpreter);
					}
				} else 
					if (node.getKeyword().equals(TERMINATE_AGENT_KEYWORD)) {
						ASTNode agentNode = node.getAgent();
						
						if (!agentNode.isEvaluated())
							return agentNode; 
						
						if (agentNode.getValue() == null) {
							capi.error("The agent argument passed to 'terminate' does not have a value.", pos, interpreter);
						} else {
							final Location loc = new Location(AbstractStorage.PROGRAM_FUNCTION_NAME, new ElementList(agentNode.getValue()));
							final UpdateMultiset updates = new UpdateMultiset();
							
							updates.add(new Update(loc, Element.UNDEF, Update.UPDATE_ACTION, interpreter.getSelf(), pos.getScannerInfo()));
							updates.add(new Update(getSchedulingStatusLocation(agentNode.getValue()), 
											terminatedFlag, Update.UPDATE_ACTION, interpreter.getSelf(), pos.getScannerInfo()));

							pos.setNode(null, updates, null, null);

							logger.debug("Terminating agent '{}'.", agentNode.getValue());
						}
					} else 
						if (node.getKeyword().equals(SHUTDOWN_KEYWORD)) {
							// TODO can also send a "terminate" command to engine
							//      but that would prevent this step to be completed.
							
							final AbstractUniverse agents = capi.getStorage().getUniverse(AbstractStorage.AGENTS_UNIVERSE_NAME);
							final UpdateMultiset updates = new UpdateMultiset();
							for (Element a: ((Enumerable)agents).enumerate()) {
								final Location agentsLoc = new Location(AbstractStorage.AGENTS_UNIVERSE_NAME, new ElementList(a));
								updates.add(new Update(agentsLoc, BooleanElement.FALSE, Update.UPDATE_ACTION, interpreter.getSelf(), pos.getScannerInfo()));
							}
							pos.setNode(null, updates, null, null);

							logger.debug("Shutting down.");
						}
			
		} 
		
		return pos;
	}
	
	/*
	 * returns the location of the agent scheduling function for
	 * the given agent 
	 */
	private Location getSchedulingStatusLocation(Element agent) {
		return new Location(AGENT_SCHEDULING_STATUS_FUNC_NAME, new ElementList(agent));
	}
	
	public Set<String> getBackgroundNames() {
		return Collections.emptySet();
	}

	public Map<String, BackgroundElement> getBackgrounds() {
		return Collections.emptyMap();
	}

	public Set<String> getFunctionNames() {
		return getFunctions().keySet();
	}

	public Map<String, FunctionElement> getFunctions() {
		if (funcs == null) {
			funcs = new HashMap<String, FunctionElement>();
			funcs.put(AGENT_SCHEDULING_STATUS_FUNC_NAME, agentSchedulingStatusFunction);
		}
		return funcs;
	}

	public Set<String> getRuleNames() {
		return Collections.emptySet();
	}

	public Map<String, RuleElement> getRules() {
		return Collections.emptyMap();
	}

	public Set<String> getUniverseNames() {
		return Collections.emptySet();
	}

	public Map<String, UniverseElement> getUniverses() {
		return Collections.emptyMap();
	}

	public void fireOnModeTransition(EngineMode source, EngineMode target)
			throws EngineException {
		if (target.equals(EngineMode.emStartingStep)) {
			suspendedOrTerminatedAgents.clear();
			final Map<ElementList, Element> table = agentSchedulingStatusFunction.getTable();
			for (Entry<ElementList, Element> entry: table.entrySet()) {
				if (entry.getValue() == suspendedFlag || entry.getValue() == terminatedFlag) {
					final Element agent = entry.getKey().get(0);
					if (agent != null)
						suspendedOrTerminatedAgents.add(agent);
				}
			}
		}		
	}

	public Map<EngineMode, Integer> getSourceModes() {
		return Collections.emptyMap();
	}

	public Map<EngineMode, Integer> getTargetModes() {
		if (targetModes == null) {
			targetModes = new HashMap<EngineMode, Integer>();
			targetModes.put(EngineMode.emStartingStep, ExtensionPointPlugin.DEFAULT_PRIORITY);
		}
		return targetModes;
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
