/*	
 * BasicASMPlugins.java  	1.0
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
 */
 
package org.coreasim.engine.plugins;

import java.util.HashSet;
import java.util.Set;

import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.plugin.PackagePlugin;
import org.coreasim.engine.plugin.Plugin;

/** 
 * This package plug-in includes all the plug-ins that 
 * together provide the basic ASM rule forms and functionalities.
 * 
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class BasicASMPlugins extends Plugin implements PackagePlugin {

	public static final String PLUGIN_NAME = BasicASMPlugins.class.getSimpleName();
	
	public static final VersionInfo VERSION_INFO = new VersionInfo(1, 0, 0, "");

	private Set<String> names = null;
	
	public BasicASMPlugins() {
		names = new HashSet<String>();
		names.add("BlockRulePlugin");
		names.add("ChooseRulePlugin");
		names.add("ConditionalRulePlugin");
		names.add("ForallRulePlugin");
		names.add("LetRulePlugin");
		names.add("CaseRulePlugin");
		names.add("NumberPlugin");
		names.add("PredicateLogicPlugin");
		names.add("BlockPolicyPlugin");
		names.add("ChoosePolicyPlugin");
		names.add("ConditionalPolicyPlugin");
		names.add("ForallPolicyPlugin");
		names.add("CasePolicyPlugin");
		names.add("LetPolicyPlugin");
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.VersionInfoProvider#getVersionInfo()
	 */
	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	public Set<String> getEnclosedPluginNames() {
		return names;
	}

}
