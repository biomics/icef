/*	
 * StandardPlugins.java 	1.0 	
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
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
 */
 
package org.coreasim.engine.plugins;

import java.util.HashSet;
import java.util.Set;

import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.plugin.PackagePlugin;
import org.coreasim.engine.plugin.Plugin;

/** 
 * The Standard Plugin package.
 *   
 * @author Roozbeh Farahbod
 * 
 */
public class StandardPlugins extends Plugin implements PackagePlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 9, 0, "beta");
	
	private Set<String> names = null;

	public StandardPlugins() {
		names = new HashSet<String>();
		names.add("StringPlugin");
		names.add("IOPlugin");
		names.add("TimePlugin");
		names.add("BlockRulePlugin");
		names.add("ChooseRulePlugin");
		names.add("ConditionalRulePlugin");
		names.add("ExtendRulePlugin");
		names.add("ForallRulePlugin");
		names.add("LetRulePlugin");
		names.add("NumberPlugin");
		names.add("PredicateLogicPlugin");
		names.add("SetPlugin");
		names.add("SignaturePlugin");
		names.add("TurboASMPlugin");
		names.add("CollectionPlugin");
		names.add("ListPlugin");
		names.add("StackPlugin");
		names.add("MathPlugin");
		names.add("QueuePlugin");
		names.add("MapPlugin");
		names.add("AbstractionPlugin");
		names.add("CaseRulePlugin");
		names.add("OptionsPlugin");
		names.add("KernelExtensionsPlugin");
		names.add("BlockPolicyPlugin");
		names.add("ChoosePolicyPlugin");
		names.add("ConditionalPolicyPlugin");
		names.add("ForallPolicyPlugin");
		names.add("CasePolicyPlugin");
		names.add("LetPolicyPlugin");
		names.add("CommunicationPlugin");
	}
	
	/**
	 * Does nothing.
	 * 
	 * @see org.coreasim.engine.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {
		// Nothing.
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.PackagePlugin#getEnclosedPluginNames()
	 */
	public Set<String> getEnclosedPluginNames() {
		return names;
	}

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

}
