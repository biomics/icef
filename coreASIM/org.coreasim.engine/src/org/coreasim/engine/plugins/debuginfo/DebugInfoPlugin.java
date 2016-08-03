/*	

 * DebugInfoPlugin.java  	1.0
 * 
 * Copyright (C) 2009 Roozbeh Farahbod
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

package org.coreasim.engine.plugins.debuginfo;


import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.TriggerMultiset;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.InterpreterException;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugin.InitializationFailedException;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugin.PluginServiceInterface;
import org.coreasim.engine.plugin.ServiceProvider;
import org.coreasim.engine.plugin.ServiceRequest;
import org.coreasim.util.Tools;

/**
 * A plugin to maintain logging information for debugging purposes. It adds the following rule construct to the CoreASM language:
 * <p>
 * <code>'debuginfo'</b> ID TERM</code>
 * <p>
 * which, upon evaluation, adds the string representation of the given term to the logging channel identified by  
 * given ID. 
 *   
 * The set of active channels are to be defined as a space-separated list of channel ids, set as the value
 * of the <code>DebugInfo.activeChannels</code> engine property. (see {@link DebugInfoPlugin#ACTIVE_CHANNELS_PROPERTY})
 *  
 * @author Roozbeh Farahbod
 *
 */
public class DebugInfoPlugin extends Plugin implements ParserPlugin, InterpreterPlugin, ServiceProvider {

	public static final String PLUGIN_NAME = DebugInfoPlugin.class.getSimpleName();
	
	public static final VersionInfo VERSION_INFO = new VersionInfo(1, 1, 1, "");

	private static final String ACTIVE_CHANNELS_PROPERTY = "DebugInfo.activeChannels";
	
	public static final String DEBUGINFO_KEYWORD = "debuginfo";
	
	public static final String ALL_CHANNELS_ID = "ALL";

	public static final String DEBUG_SERVICE_TYPE = "debuginfo";
	
	/** Output stream of this plugin */
	protected PrintStream outputStream;
	protected PluginServiceInterface pluginPSI;

	private Set<String> activeChannels = null;
	private Map<String, GrammarRule> parsers = null;

	private final String[] keywords = {DEBUGINFO_KEYWORD};
	private final String[] operators = {};

	@Override
	public void initialize() throws InitializationFailedException {
		outputStream = System.out;
		pluginPSI = new DebugInfoPSI();
		activeChannels = null;
		capi.addServiceProvider(DEBUG_SERVICE_TYPE, this);
	}
	
	protected void updateChannelsList() {
		if (activeChannels == null) {
			activeChannels = new HashSet<String>();
			String channels = capi.getProperty(ACTIVE_CHANNELS_PROPERTY);
			if (channels != null && channels.length() > 0) {
				channels = Tools.trimAllDoubleQuotes(channels);
				StringTokenizer tokenizer = new StringTokenizer(channels, " ," + Tools.getEOL());
				while (tokenizer.hasMoreTokens()) {
					String cid = tokenizer.nextToken();
					
					// if 'ALL' is mentioned as a channel, ignore the list and 
					// add only 'ALL' to the channel list. This will make all
					// channels to become active.
					if (cid.equals(ALL_CHANNELS_ID)) {
						activeChannels.clear();
						activeChannels.add(ALL_CHANNELS_ID);
						break;
					} else
						activeChannels.add(cid);
				}
			}
		}
	}

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
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
		GrammarRule rule = getParsers().get(nonterminal);
		if (rule != null)
			return rule.parser;
		else
			return null;
	}

	public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> termParser = kernel.getTermParser();
			
			ParserTools pTools = ParserTools.getInstance(capi);
			Parser<Node> idParser = pTools.getIdParser();
			
			// 'debuginfo' ID TERM
			Parser<Node> debugInfoParser = Parsers.array(
					new Parser[] {
					pTools.getKeywParser(DEBUGINFO_KEYWORD, PLUGIN_NAME),
					idParser,
					termParser
					}).map(
					new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						public Node map(Object[] vals) {
							Node node = new DebugInfoNode(((Node)vals[0]).getScannerInfo());
							node.addChild((Node)vals[0]);
							node.addChild("alpha", (Node)vals[1]);
							node.addChild("beta", (Node)vals[2]);
							return node;
						}
				
					});

			parsers.put("Rule", 
					new GrammarRule("DebugInfoRule",
							"'debuginfo' ID TERM", debugInfoParser, PLUGIN_NAME));
		}
		
		return parsers;
	}

	public ASTNode interpret(Interpreter interpreter, ASTNode pos)
			throws InterpreterException {
		
		if (pos instanceof DebugInfoNode) {
			DebugInfoNode node = (DebugInfoNode)pos;
			updateChannelsList();
			
			if (!node.getMessage().isEvaluated())
				return node.getMessage();
			
			String channel = node.getId().getToken();
			String msg = node.getMessage().getValue().toString();
			writeDebugInfo(channel, msg);
			
			pos.setNode(null, new UpdateMultiset(), new TriggerMultiset(), null);
		}
		
		return pos;
	}

	public PluginServiceInterface getPluginInterface() {
		return pluginPSI;
	}

	private void writeDebugInfo(String ch, String msg) {
		if (activeChannels != null && activeChannels.size() > 0) {
			if (activeChannels.contains(ALL_CHANNELS_ID) || activeChannels.contains(ch))
				if (outputStream != null)
					outputStream.println("DEBUG INFO (" + ch + "): " + msg);
		}
	}
	
	/**
	 * Interface of the DebugInfoPlugin to engine environment
	 * 
	 * @author Roozbeh Farahbod
	 */
	public class DebugInfoPSI implements PluginServiceInterface {
		
		/**
		 * Sets the output stream for printing out debug info.
		 * @param output a <code>PrintStream</code> object
		 */	
		public void setOutputStream(PrintStream output) {
			synchronized (pluginPSI) {
				outputStream = output;
			}
		}
	}

	@Override
	public Object call(ServiceRequest request) {
		Object msg = request.getParam("message");
		Object ch = request.getParam("channel");
		if (msg != null && ch != null)
			writeDebugInfo(ch.toString(), msg.toString());
		return null;
	}


}
