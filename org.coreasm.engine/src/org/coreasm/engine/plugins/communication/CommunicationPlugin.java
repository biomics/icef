/*	
 * CommunicationPlugin.java 	1.
 * 
 * Copyright (C) 2016 Eric Rothstein, Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.plugins.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasm.engine.CoreASMEngine.EngineMode;
import org.coreasm.engine.CoreASMError;
import org.coreasm.engine.VersionInfo;
import org.coreasm.engine.absstorage.AgentCreationElement;
import org.coreasm.engine.absstorage.BackgroundElement;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.ElementList;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.absstorage.NameConflictException;
import org.coreasm.engine.absstorage.PluginAggregationAPI;
import org.coreasm.engine.absstorage.PluginAggregationAPI.Flag;
import org.coreasm.engine.absstorage.PluginCompositionAPI;
import org.coreasm.engine.absstorage.PolicyElement;
import org.coreasm.engine.absstorage.RuleElement;
import org.coreasm.engine.absstorage.UniverseElement;
import org.coreasm.engine.absstorage.UnmodifiableFunctionException;
import org.coreasm.engine.absstorage.Update;
import org.coreasm.engine.absstorage.UpdateMultiset;
import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.Interpreter;
import org.coreasm.engine.interpreter.InterpreterException;
import org.coreasm.engine.interpreter.Node;
import org.coreasm.engine.interpreter.ScannerInfo;
import org.coreasm.engine.kernel.KernelServices;
import org.coreasm.engine.parser.GrammarRule;
import org.coreasm.engine.parser.ParserTools;
import org.coreasm.engine.plugin.Aggregator;
import org.coreasm.engine.plugin.ExtensionPointPlugin;
import org.coreasm.engine.plugin.InterpreterPlugin;
import org.coreasm.engine.plugin.ParserPlugin;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.engine.plugin.PluginServiceInterface;
import org.coreasm.engine.plugin.VocabularyExtender;
import org.coreasm.engine.plugins.forallpolicy.ForallPolicyNode;
import org.coreasm.engine.plugins.set.SetElement;
import org.coreasm.engine.plugins.string.StringElement;

/** 
 * A plugin that extends the Input/Output plugin using communication services 
 *   
 * @author Eric Rothstein
 * 
 */
public class CommunicationPlugin extends Plugin implements 
		ParserPlugin, InterpreterPlugin, VocabularyExtender, ExtensionPointPlugin, Aggregator {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 3, 2, "");

	public static final String PLUGIN_NAME = CommunicationPlugin.class.getSimpleName();

	/** The send rule */
	public static final String SEND_KEYWORD = "send";
	public static final String WITH_KEYWORD = "with";
	public static final String SUBJECT_KEYWORD = "subject";
	public static final String TO_KEYWORD = "to";
	public static final String MAIL_TO_ACTION = "mailToAction";
	public static final String MAIL_FROM_ACTION = "mailFromAction";
	
	/**Create agent**/
	public static final String CREATE_KEYWORD = "create";
	public static final String AGENT_KEYWORD = "agent";
	public static final String INITIALIZED_KEYWORD = "initialized";
	public static final String BY_KEYWORD = "by";
	public static final String ID_KEYWORD = "id";
	public static final String IN_KEYWORD = "in";
	public static final String USING_KEYWORD = "using";
	
	/**Inbox and outbox**/
	public static final String OUTBOX_FUNC_NAME = "outboxOf";
	public static final Location OUTBOX_FUNC_LOC = new Location(CommunicationPlugin.OUTBOX_FUNC_NAME,
			ElementList.NO_ARGUMENT);
	public static final String[] UPDATE_ACTIONS = { MAIL_TO_ACTION, MAIL_FROM_ACTION};
	
	/** The input function */
	public static final String INBOX_FUNC_NAME = "inboxOf";
	public static final Location INBOX_FUNC_LOC = new Location(CommunicationPlugin.INBOX_FUNC_NAME, ElementList.NO_ARGUMENT);
	
	/** The getMessageValue functions */
	public static final String GET_MESSAGE_CONTENT_FUNC_NAME = "messageContent";
	public static final String GET_MESSAGE_SUBJECT_FUNC_NAME = "messageSubject";
	public static final String GET_MESSAGE_SENDER_FUNC_NAME = "messageSender";
	public static final String GET_MESSAGE_RECEIVER_FUNC_NAME = "messageReceiver";
	public static final String GET_MESSAGE_STEP_FUNC_NAME = "messageStep";
	private final Set<String> dependencyList;
	
	/** 
	 * List of all the messages generated in the current run. 
	 * This list will be empty if an output stream is set (i.e., {@link #outputStream} is not null). 
	 */
	public List<MessageElement> allMessages;
	
	private Map<EngineMode, Integer> sourceModes;
	private Map<EngineMode, Integer> targetModes;
	private HashSet<String> functionNames;
	private Map<String,FunctionElement> functions = null;
	
	protected Map<String, GrammarRule> parsers = null;
	protected CommunicationPSI pluginPSI;
	protected InboxFunctionElement inboxFunction;
	protected OutboxFunctionElement outboxFunction;



	private final String[] keywords = { SEND_KEYWORD, TO_KEYWORD, WITH_KEYWORD, SUBJECT_KEYWORD, CREATE_KEYWORD, AGENT_KEYWORD, INITIALIZED_KEYWORD, BY_KEYWORD, USING_KEYWORD};
	private final String[] operators = { };
	
	/**
	 * 
	 */
	public CommunicationPlugin() {
		super();
		dependencyList = new HashSet<String>();
		dependencyList.add("StringPlugin");
		dependencyList.add("IOPlugin");
		pluginPSI = new CommunicationPSI();

		functionNames = new HashSet<String>();
		functionNames.add(INBOX_FUNC_NAME);
		functionNames.add(OUTBOX_FUNC_NAME);
		functionNames.add(GET_MESSAGE_CONTENT_FUNC_NAME);
		functionNames.add(GET_MESSAGE_SENDER_FUNC_NAME);
		functionNames.add(GET_MESSAGE_RECEIVER_FUNC_NAME);
		functionNames.add(GET_MESSAGE_SUBJECT_FUNC_NAME);
		functionNames.add(GET_MESSAGE_STEP_FUNC_NAME);
	}


	public String[] getKeywords() {
		return keywords;
	}

	public String[] getOperators() {
		return operators;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {
		inboxFunction = new InboxFunctionElement();
		outboxFunction = new OutboxFunctionElement();
		allMessages = new ArrayList<MessageElement>();
		pluginPSI = new CommunicationPSI();
		sourceModes = new HashMap<EngineMode, Integer>();
		sourceModes.put(EngineMode.emAggregation, ExtensionPointPlugin.DEFAULT_PRIORITY);
		targetModes = new HashMap<EngineMode, Integer>();
		targetModes.put(EngineMode.emStepSucceeded, ExtensionPointPlugin.DEFAULT_PRIORITY);
		targetModes.put(EngineMode.emInitializingState, ExtensionPointPlugin.DEFAULT_PRIORITY);
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
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> termParser = kernel.getTermParser();
			
			ParserTools parserTools = ParserTools.getInstance(capi);

			Parser<Node> sendToParser = Parsers.array(
					new Parser[] {
							parserTools.getKeywParser(SEND_KEYWORD, PLUGIN_NAME),
							termParser,
							parserTools.getKeywParser(TO_KEYWORD, PLUGIN_NAME),
							termParser,
							parserTools.getKeywParser(WITH_KEYWORD, PLUGIN_NAME),
							parserTools.getKeywParser(SUBJECT_KEYWORD, PLUGIN_NAME),
							termParser
					}).map(
							//This should be the parser of the SendTo rule
							new org.codehaus.jparsec.functors.Map<Object[], Node>() {
						public Node map(Object[] vals) {	
							
							Node node = new SendToRuleNode(((Node) vals[0]).getScannerInfo());
							node.addChild((Node) vals[0]);
							node.addChild("alpha", (Node) vals[1]);
							node.addChild((Node) vals[2]);
							node.addChild("beta", (Node) vals[3]);	
							node.addChild((Node) vals[4]);
							node.addChild((Node) vals[5]);
							node.addChild("gamma", (Node) vals[6]);	
							return node;
						}
					});


			parsers.put("SendToRule",
					new GrammarRule("SendToRule", "'send' Term 'to' Term 'with' 'subject' Term", sendToParser, PLUGIN_NAME));
			
			Parser<Node> createAgentParser = Parsers.array(
					new Parser[] {
							parserTools.getKeywParser(CREATE_KEYWORD, PLUGIN_NAME),
							parserTools.seq(parserTools.getKeywParser(AGENT_KEYWORD, PLUGIN_NAME), termParser.optional()),
							parserTools.getKeywParser(INITIALIZED_KEYWORD, PLUGIN_NAME),
							parserTools.getKeywParser(BY_KEYWORD, PLUGIN_NAME),
							termParser,
							parserTools.getKeywParser(USING_KEYWORD, PLUGIN_NAME),
							termParser,
							parserTools.getKeywParser(IN_KEYWORD, PLUGIN_NAME),
							termParser
	
					}).map(
							//This should be the parser of the SendTo rule
							new CreateAgentParseMap()); 
//								
//								
//						public Node map(Object[] vals) {	
//							
//							Node node = new CreateAgentRuleNode(((Node) vals[0]).getScannerInfo());
//							node.addChild((Node) vals[0]);
//							node.addChild((Node) vals[1]);
//							node.addChild((Node) vals[2]);
//							node.addChild((Node) vals[3]);
//							node.addChild("init", (Node) vals[2]);
//							node.addChild((Node) vals[4]);
//							node.addChild("program", (Node) vals[5]);	
//							node.addChild((Node) vals[6]);
//							node.addChild("location", (Node) vals[7]);
//							
//							node.addChild((Node) vals[8]);
//							node.addChild("location", (Node) vals[9]);	
//							return node;
//						}
//					});


			parsers.put("CreateAgentRule",
					new GrammarRule("CreateAgentRule", "'create' 'agent' (Term)? 'initialized' 'by' Term 'using' Term 'in' Location", createAgentParser, PLUGIN_NAME));
			
			Parser<Node> communicationRuleParser = Parsers.or(sendToParser,createAgentParser);
			parsers.put("Rule",new GrammarRule("CommunicationRule", "SendToRule | CreateAgentRule", communicationRuleParser, PLUGIN_NAME));
		}

		return parsers;
	}

	public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
		// SendTo Rule
		if (pos instanceof SendToRuleNode) {
			return interpretSendTo(interpreter, (SendToRuleNode)pos); 
		}
		// CreateAgent Rule
		if (pos instanceof CreateAgentRuleNode) {
			return interpretCreateAgent(interpreter, (CreateAgentRuleNode)pos); 
		}
		return pos;
	}
	
	/*
	 * Interprets the create agent rule.
	 */
	private ASTNode interpretCreateAgent(Interpreter interpreter, CreateAgentRuleNode pos) {
		if( pos.getAgentName()!= null)
		{
			if (!pos.getAgentName().isEvaluated()) {
				return pos.getAgentName();
			}
		}
		else if (!pos.getAgentInit().isEvaluated()) {
			return pos.getAgentInit();
		} 
		else if (!pos.getAgentProgram().isEvaluated()) {
			return pos.getAgentProgram();
		} 
		else if (!pos.getAgentLocation().isEvaluated()) {
			return pos.getAgentLocation();
		} 
		else{
			AgentCreationElement ace;
			Element newElement = (pos.getAgentName()!= null)? pos.getAgentName().getValue(): capi.getStorage().getNewElement();
			capi.getAgentsToCreate().put(pos.getAgentLocation().getLocation().toString(), (pos.getAgentName()!= null)?newElement.toString():"");
			
			try {
				ace = new AgentCreationElement(new StringElement(newElement.toString()),pos.getAgentInit().getValue(),pos.getAgentProgram().getValue());
			pos.setNode(
					null, 
					new UpdateMultiset(new Update(pos.getAgentLocation().getLocation(),newElement, Update.UPDATE_ACTION,interpreter.getSelf(),pos.getScannerInfo())
//							,new Update(
//									OUTBOX_FUNC_LOC,
//									new MessageElement(interpreter.getSelf().toString(), ace, "Scheduler", "AgentCreation" ,capi.getStepCount(),ace.getClass().getSimpleName()),
//									MAIL_TO_ACTION,
//									interpreter.getSelf(),
//									pos.getScannerInfo()
//									)
							), 
					null,
					null);
			} catch (Throwable e) {
				capi.error(e);
			}
		}
		return pos;
	}


	/*
	 * Interprets the Send to rule.
	 */
	private ASTNode interpretSendTo(Interpreter interpreter, SendToRuleNode pos) throws InterpreterException {
		if (!pos.getMessage().isEvaluated()) {
			return pos.getMessage();
		} 
		else if (!pos.getAddress().isEvaluated()) {
			return pos.getAddress();
		} 
		else if (!pos.getSubject().isEvaluated()) {
			return pos.getSubject();
		} 
		else {
			//This thing is creating two updates: one for the outbox and one for the inbox. It is not 
			// pretty, but it should work.
			pos.setNode(
					null, 
					new UpdateMultiset(
							new Update(
									OUTBOX_FUNC_LOC,
									new MessageElement(interpreter.getSelf().toString(), pos.getMessage().getValue(), pos.getAddress().getValue().toString(), pos.getSubject().getValue().toString(),capi.getStepCount(), pos.getMessage().getValue().getClass().getSimpleName()),
									MAIL_TO_ACTION,
									interpreter.getSelf(),
									pos.getScannerInfo()
									)), 
					null,
					null);
		}
		return pos;
	}
	
	/**
	 * Returns a set containing the following functions:
	 * <ul>
	 * <li><i>output</i></li>
	 * </ul>
	 */
	public Map<String,FunctionElement> getFunctions() {
		if (functions == null) {
			functions = new HashMap<String,FunctionElement>();
			if(inboxFunction == null)
				inboxFunction = new InboxFunctionElement();
			if(outboxFunction == null)
				outboxFunction = new OutboxFunctionElement();
			functions.put(OUTBOX_FUNC_NAME, outboxFunction);
			functions.put(INBOX_FUNC_NAME, inboxFunction);
			functions.put(GET_MESSAGE_CONTENT_FUNC_NAME, new GetMessageContentFunctionElement());
			functions.put(GET_MESSAGE_SUBJECT_FUNC_NAME, new GetMessageSubjectFunctionElement());
			functions.put(GET_MESSAGE_SENDER_FUNC_NAME, new GetMessageSenderFunctionElement());
			functions.put(GET_MESSAGE_RECEIVER_FUNC_NAME, new GetMessageReceiverFunctionElement());
			functions.put(GET_MESSAGE_STEP_FUNC_NAME, new GetMessageStepFunctionElement());
		}
		return functions;
	}

	public Map<String,UniverseElement> getUniverses() {
		return Collections.emptyMap();
	}

	public Map<String,BackgroundElement> getBackgrounds() {
		return Collections.emptyMap();
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#getPluginInterface()
	 */
	@Override
	public PluginServiceInterface getPluginInterface() {
		return pluginPSI;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#getDependencyNames()
	 */
	@Override
	public Set<String> getDependencyNames() {
		return this.dependencyList;
	}

	/**
	 * Write updates to files and print updates on the console.
	 * 
	 * @param source
	 * @param target
	 * @throws UnmodifiableFunctionException
	 */
	public void fireOnModeTransition(EngineMode source, EngineMode target) throws UnmodifiableFunctionException {
		//on initialization clear output messages
		if (EngineMode.emInitializingState.equals(target))
			allMessages.clear();
		//aggregate and compose updates for print to console and print (in)to file
		if (source.equals(EngineMode.emAggregation) && target.equals(EngineMode.emStepSucceeded)) {
		//	outboxManagement();
		//	inboxManagement();
			//FIXME Hook left here because we may want to do things inbetween steps
		}
	}

	/**
	 * Print the value of the print location to the outputSteam which depends on the user interface of CoreASM, i.e. EngineDriver
	 */
	private void outboxManagement() {
		try {
			FunctionElement outboxFunction = capi.getStorage().getFunction(CommunicationPlugin.OUTBOX_FUNC_NAME);
			Element value =  outboxFunction.getValue(OUTBOX_FUNC_LOC.args);
			SetElement msgs;
			if (value instanceof SetElement)
			{	
				msgs = (SetElement) value;
				for(Element e:  msgs.getSet())
				{	if(e instanceof MessageElement)
						allMessages.add((MessageElement) e);
				}
				outboxFunction.setValue(OUTBOX_FUNC_LOC.args, msgs);
			}
			
		}
		catch (UnmodifiableFunctionException e) {
			// Should not happen
			throw new CoreASMError("Outbox function is unmodifiable.");
		}
	}
	
	/**
	 * Print the value of the print location to the outputSteam which depends on the user interface of CoreASM, i.e. EngineDriver
	 */
	private void inboxManagement() {
		try {
			FunctionElement inboxFunction = capi.getStorage().getFunction(CommunicationPlugin.INBOX_FUNC_NAME);
			Element value =  inboxFunction.getValue(INBOX_FUNC_LOC.args);
			//TODO BSL here is where we can connect the commlibrary to provide some set of updates...
//			if (outputStream == null)
//				for(Element e: msgs.getSet())
//					if(e instanceof MessageElement)
//						allMessages.add((MessageElement) e);
//			outputStream.print(msgs);
			//TODO BSL Here is where we determine whether we want to reset the outbox or not, or if we want to distribute 
			//the outbox the different agents.... maybe it is better than filtering the global outbox
			if (value instanceof SetElement)
				inboxFunction.setValue(INBOX_FUNC_LOC.args, value);
		}
		catch (UnmodifiableFunctionException e) {
			// Should not happen
			throw new CoreASMError("inbox function is unmodifiable.");
		}
	}

	public Map<EngineMode, Integer> getSourceModes() {
		return sourceModes;
	}

	public Map<EngineMode, Integer> getTargetModes() {
		return targetModes;
	}

	public Set<String> getBackgroundNames() {
		return Collections.emptySet();
	}

	public Set<String> getFunctionNames() {
		return functionNames;
	}

	public Set<String> getUniverseNames() {
		return Collections.emptySet();
	}

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	public void aggregateUpdates(PluginAggregationAPI pluginAgg) {
		synchronized (this) {
			aggregateOutbox(pluginAgg);
		}
	}



	/**
	 * Aggregate updates for the outbox location.
	 * @param pluginAgg
	 */
	public void aggregateOutbox(PluginAggregationAPI pluginAgg) {
		// all locations on which contain print actions
		Set<Location> locsToAggregate = pluginAgg.getLocsWithAnyAction(MAIL_TO_ACTION);
		Set<Element> contributingAgents = new HashSet<Element>();
		Set<ScannerInfo> contributingNodes = new HashSet<ScannerInfo>();

		// for all locations to aggregate
		for (Location l : locsToAggregate) {
			if (l.equals(OUTBOX_FUNC_LOC)) {
				Set<MessageElement> aggregatedOutbox = new HashSet<MessageElement>();
				//Regular updates should NOT affect this location!
				// if regular update affects this location
				if (pluginAgg.regularUpdatesAffectsLoc(l)) {
					pluginAgg.handleInconsistentAggregationOnLocation(l, this);
				}
				else {
					for (Update update : pluginAgg.getLocUpdates(l)) {
						if (update.action.equals(MAIL_TO_ACTION)) {
							aggregatedOutbox.add((MessageElement) update.value);
							// flag update aggregation as successful for this update
							pluginAgg.flagUpdate(update, Flag.SUCCESSFUL, this);
							contributingAgents.addAll(update.agents);
							contributingNodes.addAll(update.sources);
						}
					}
				}
				outboxFunction.setValue(OUTBOX_FUNC_LOC.args, new SetElement(aggregatedOutbox));
			}
		}
	}
	
	/**
	 * Aggregate updates for the inbox location.
	 * @param pluginAgg
	 */
	public void aggregateInbox(PluginAggregationAPI pluginAgg) {
		// all locations on which contain print actions
		Set<Location> locsToAggregate = pluginAgg.getLocsWithAnyAction(MAIL_FROM_ACTION);
		Set<Element> contributingAgents = new HashSet<Element>();
		Set<ScannerInfo> contributingNodes = new HashSet<ScannerInfo>();

		// for all locations to aggregate
		for (Location l : locsToAggregate) {
			if (l.equals(INBOX_FUNC_LOC)) {
				Set<MessageElement> aggregatedInbox = new HashSet<MessageElement>();
				//Regular updates should NOT affect this location!
				// if regular update affects this location
				if (pluginAgg.regularUpdatesAffectsLoc(l)) {
					pluginAgg.handleInconsistentAggregationOnLocation(l, this);
				}
				else {
					for (Update update : pluginAgg.getLocUpdates(l)) {
						if (update.action.equals(MAIL_FROM_ACTION)) {
							aggregatedInbox.add((MessageElement) update.value);
							// flag update aggregation as successful for this update
							pluginAgg.flagUpdate(update, Flag.SUCCESSFUL, this);
							contributingAgents.addAll(update.agents);
							contributingNodes.addAll(update.sources);
						}
					}
				}
				inboxFunction.setValue(INBOX_FUNC_LOC.args, new SetElement(aggregatedInbox));
			}
		}
	}

	@Override
	public String[] getUpdateActions() {
		return UPDATE_ACTIONS;
	}

	public Set<String> getRuleNames() {
		return Collections.emptySet();
	}

	public Map<String, RuleElement> getRules() {
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


	@Override
	public void compose(PluginCompositionAPI compAPI) {
		// TODO Auto-generated method stub
		
	}
	
	public class CommunicationPSI implements PluginServiceInterface {
		
		public void updateInboxLocation(Set<MessageElement> inbox)
		{
			synchronized(pluginPSI){
				
				inboxFunction.setValue(INBOX_FUNC_LOC.args, new SetElement(inbox));
			}
		}
		
		public Set<MessageElement> collectOutgoingMessages() 
		{
			synchronized(pluginPSI){
				
				 return outboxFunction.getMessages(); 
			}
			
		}
		
	}
	/*
	 * TODO BSL: 
	 * 1) Describe functions get sender, get receiver, get message
	 * 2) Enrich message format? What can we send over the communication plugin?
	 */
	public static class CreateAgentParseMap //extends ParseMapN<Node> {
	extends ParserTools.ArrayParseMap {

		String nextChildName = "alpha";
		
		public CreateAgentParseMap() {
			super(PLUGIN_NAME);
		}

		public Node map(Object[] vals) {
			nextChildName = "alpha";
            Node node = new CreateAgentRuleNode(((Node)vals[0]).getScannerInfo());
            addChildren(node, vals);
			return node;
		}

		@Override
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode)
				parent.addChild(nextChildName, child);
			else {
				String token = child.getToken();
		        if (token.equals("agent"))
		        	nextChildName = "id";
		        else if (token.equals("by"))
	        		nextChildName = "init";
		        else if (token.equals("using"))
		        	nextChildName = "program";
		        else if (token.equals("in"))
		        	nextChildName = "location";
				super.addChild(parent, child);
			}
		}
		
	}
}
