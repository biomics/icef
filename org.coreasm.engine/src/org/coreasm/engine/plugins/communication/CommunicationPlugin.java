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

import java.io.PrintStream;
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
import org.coreasm.engine.absstorage.BackgroundElement;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.ElementList;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.MessageElement;
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
import org.coreasm.engine.plugins.set.SetElement;

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
	public static final String OUTBOX_FUNC_NAME = "outbox";
	public static final Location OUTBOX_FUNC_LOC = new Location(CommunicationPlugin.OUTBOX_FUNC_NAME,
			ElementList.NO_ARGUMENT);
	public static final String[] UPDATE_ACTIONS = { MAIL_TO_ACTION, MAIL_FROM_ACTION};
	
	/** The input function */
	public static final String INBOX_FUNC_NAME = "inbox";
	//FIXME BSL if you have problems with the modification of the mailbox, change the flag from false to true here
	public static final Location INBOX_FUNC_LOC = new Location(CommunicationPlugin.INBOX_FUNC_NAME, ElementList.NO_ARGUMENT);
	
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
	protected CommunicationPluginPSI pluginPSI;
	protected FunctionElement inboxFunction;
	protected FunctionElement outboxFunction;
//	protected FunctionElement filterInboxFunction;
//	protected FunctionElement filterOutboxFunction;
//	protected FunctionElement inputFunction;
//	protected FunctionElement fileInputFunction;
	protected InputProvider inputProvider;
	protected PrintStream outputStream;

	private final String[] keywords = { SEND_KEYWORD, TO_KEYWORD, WITH_KEYWORD, SUBJECT_KEYWORD};
	private final String[] operators = { };
	
	/**
	 * 
	 */
	public CommunicationPlugin() {
		super();
		dependencyList = new HashSet<String>();
		dependencyList.add("StringPlugin");
		dependencyList.add("IOPlugin");
		pluginPSI = new CommunicationPluginPSI();

		functionNames = new HashSet<String>();
		functionNames.add(INBOX_FUNC_NAME);
		functionNames.add(OUTBOX_FUNC_NAME);
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
		//TODO Complete the inboxFunction
		inboxFunction = new InboxFunctionElement();
		outboxFunction = new OutboxFunctionElement();
		allMessages = new ArrayList<MessageElement>();
		pluginPSI = new CommunicationPluginPSI();
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
			
			ParserTools npTools = ParserTools.getInstance(capi);

			Parser<Node> sendToParser = Parsers.array(
					new Parser[] {
							npTools.getKeywParser(SEND_KEYWORD, PLUGIN_NAME),
							termParser,
							npTools.getKeywParser(TO_KEYWORD, PLUGIN_NAME),
							termParser,
							npTools.getKeywParser(WITH_KEYWORD, PLUGIN_NAME),
							npTools.getKeywParser(SUBJECT_KEYWORD, PLUGIN_NAME),
							termParser
					}).map(
							//This should be the parser of the SendTo rule
							new org.codehaus.jparsec.functors.Map<Object[], Node>() {
						public Node map(Object[] vals) {	
							
							Node node = new SendToRuleNode(((Node) vals[0]).getScannerInfo());
							
							System.out.println("vals[0]: "+vals[0]);
							System.out.println("vals[1]: "+vals[1]);
							System.out.println("vals[2]: "+vals[2]);
							System.out.println("vals[3]: "+vals[3]);
							System.out.println("vals[2]: "+vals[4]);
							System.out.println("vals[3]: "+vals[5]);
							System.out.println("vals[2]: "+vals[6]);
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


			parsers.put("Rule",
					new GrammarRule("sendToRule", "'send' Term 'to' Term 'with' 'subject' Term", sendToParser, PLUGIN_NAME));

		}

		return parsers;
	}

	public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
		// SendTo Rule
		if (pos instanceof SendToRuleNode) {
			return interpretSendTo(interpreter, (SendToRuleNode)pos); 
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
									new MessageElement(interpreter.getSelf().toString(), pos.getMessage().getValue(), pos.getAddress().getValue().toString(), pos.getSubject().getValue().toString(),capi.getStepCount()),
									MAIL_TO_ACTION,
									interpreter.getSelf(),
									pos.getScannerInfo()
									),
							new Update(INBOX_FUNC_LOC,
									new MessageElement(interpreter.getSelf().toString(), pos.getMessage().getValue(), pos.getAddress().getValue().toString(),pos.getSubject().getValue().toString(),capi.getStepCount()),
									MAIL_FROM_ACTION,
									interpreter.getSelf(),
									pos.getScannerInfo())), 
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
			functions.put(OUTBOX_FUNC_NAME, outboxFunction);
			functions.put(INBOX_FUNC_NAME, inboxFunction);
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
	 * Interface of the IOPlugin to engine environment
	 * 
	 * @author Roozbeh Farahbod
	 * 
	 */
	public class CommunicationPluginPSI implements PluginServiceInterface {

		/**
		 * @return output messages as an array of <code>Element</code>
		 * @see Element
		 */
		public String[] getOutputHistory() {
			synchronized (pluginPSI) {
				String[] elements = new String[allMessages.size()];
				return allMessages.toArray(elements);
			}
		}

		/**
		 * Sets the input provider of this plugin.
		 * @param ip an input provider
		 */
		public void setInputProvider(InputProvider ip) {
			synchronized (pluginPSI) {
				inputProvider = ip;
			}
		}

		/**
		 * Sets the output stream for 'send to'  rules.
		 * @param output a <code>PrintStream</code> object
		 */	
		public void setOutputStream(PrintStream output) {
			synchronized (pluginPSI) {
				outputStream = output;
				//FIXME BSL Connect to the CommLib here?
			}
		}
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
//TODO BSL this transforms updates to text to be stored on a file. This method may serve as good inspiration to write on a socket.
//	/**
//	 * Writes all updates into files taking into account weather they should be appended to the file or not. Existing files are overwritten without any further warnings.
//	 * 
//	 * @throws UnmodifiableFunctionException
//	 */
//	private void writePrintInToFileUpdates() throws UnmodifiableFunctionException {
//		FunctionElement fileOutputFunction = capi.getStorage().getFunction(CommunicationPlugin.FILE_OUTPUT_FUNC_NAME);
//		for (Update u : capi.getScheduler().getUpdateSet()) {
//			if (APPEND_ACTION.equals(u.action) || WRITE_ACTION.equals(u.action)) {
//				ListElement outputList = (ListElement) u.value;
//				if (outputList != Element.UNDEF) {
//					//set location to undef to prevent unnecessary output to file
//					fileOutputFunction.setValue(u.loc.args, Element.UNDEF);
//					List<? extends Element> lines = outputList.getList();
//					//if the path is relative to the MAIN specification file, make it absolute.
//					String path2spec = "";
//					String fileName = u.loc.args.get(0).toString();
//					if (!new File(fileName).isAbsolute())
//						path2spec = capi.getSpec().getFileDir();
//					String outputFile = Tools.concatFileName(path2spec, fileName);
//					FileWriter fw = null;
//					try {
//						fw = new FileWriter(outputFile, APPEND_ACTION.equals(u.action));
//						for (Element line : lines)
//							fw.append(line + System.lineSeparator());
//					}
//					catch (IOException e) {
//						throw new CoreASMError("File " + outputFile + " could not be created.");
//					}
//					finally {
//						if (fw != null)
//							try {
//								fw.close();
//							}
//						catch (IOException e) {
//								e.printStackTrace();
//						}
//					}
//				}
//			}
//		}
//	}

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
			//TODO BSL here is where we might plug some communication to the Comm Library
			/*if (outputStream == null)
				for(Element e: msgs.getSet())
					if(e instanceof MessageElement)
						allMessages.add((MessageElement) e);
			 * outputStream.print(msgs);
			 */
			//TODO BSL Here is where we determine whether we want to reset the outbox or not, or if we want to distribute 
			//the outbox the different agents.... maybe it is better than filtering the global outbox 
			
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
			aggregateInbox(pluginAgg);
			//aggregateWrite(pluginAgg);
			//aggregateAppend(pluginAgg);
		}
	}

//	/**
//	 * Aggregate updates for each write locations. Whenever write as well as append updates for the same location exists, the updates are not consistent.
//	 * @param pluginAgg
//	 */
//	public void aggregateWrite(PluginAggregationAPI pluginAgg) {
//		Set<Location> writeLocsToAggregate = pluginAgg.getLocsWithAnyAction(WRITE_ACTION);
//		Set<Location> appendLocsToAggregate = pluginAgg.getLocsWithAnyAction(APPEND_ACTION);
//
//		for (Location writeLoc : writeLocsToAggregate) {
//			// if regular update affects this location
//			if (pluginAgg.regularUpdatesAffectsLoc(writeLoc)) {
//				pluginAgg.handleInconsistentAggregationOnLocation(writeLoc, this);
//			}
//			else {
//				Element locValue = null;
//				//mark at least one inconsistent update
//				for (Update update : pluginAgg.getLocUpdates(writeLoc)) {
//					if (WRITE_ACTION.equals(update.action)) {
//						//different values for the same location
//						if (locValue != null && locValue.equals(update.value))
//							pluginAgg.flagUpdate(update, Flag.FAILED, this);
//						else {
//							locValue = update.value;
//							//append and write within the same step for the same location
//							if (appendLocsToAggregate.contains(writeLoc)) {
//								pluginAgg.flagUpdate(update, Flag.FAILED, this);
//							}
//							else {
//								pluginAgg.flagUpdate(update, Flag.SUCCESSFUL, this);
//							}
//						}
//						if (!(update.value instanceof ListElement)) {
//							pluginAgg.addResultantUpdate(
//									new Update(
//											writeLoc,
//											new ListElement(Arrays.asList(new Element[] { update.value })),
//											update.action,
//											update.agents,
//											update.sources),
//									this);
//						}
//						else {
//							pluginAgg.addResultantUpdate(
//									update,
//									this);
//						}
//					}
//				}
//			}
//		}
//	}

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
				try {
					outboxFunction.setValue(OUTBOX_FUNC_LOC.args, new SetElement(aggregatedOutbox));
				} catch (UnmodifiableFunctionException e) {	
					e.printStackTrace();
				}
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
				try {
					inboxFunction.setValue(INBOX_FUNC_LOC.args, new SetElement(aggregatedInbox));
				} catch (UnmodifiableFunctionException e) {	
					e.printStackTrace();
				}
//				pluginAgg.addResultantUpdate(
//						new Update(
//								INBOX_FUNC_LOC,
//								new SetElement(aggregatedInbox),
//								Update.UPDATE_ACTION,
//								contributingAgents,
//								contributingNodes),
//						this);
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
	/*
	 * TODO BSL: 
	 * 1) Describe functions get sender, get receiver, get message
	 * 2) Enrich message format? What can we send over the communication plugin?
	 */

}
