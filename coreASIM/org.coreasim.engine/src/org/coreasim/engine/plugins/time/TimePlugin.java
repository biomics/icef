/*	
 * TimePlugin.java 	1.0 	
 * 
 * Copyright (C) 2006 Roozbeh Farahbod
 *
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
 
package org.coreasim.engine.plugins.time;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.time.CompilerTimePlugin;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.PolicyElement;
import org.coreasim.engine.absstorage.RuleElement;
import org.coreasim.engine.absstorage.UniverseElement;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugin.VocabularyExtender;

/** 
 * Provides time-related functions.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class TimePlugin extends Plugin implements VocabularyExtender {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 2, 0, "");
	
	private final Set<String> dependencyList;
	
	private NowFunctionElement nowFunction;
	
	private Map<String, FunctionElement> functions = null;
	
	private CompilerPlugin compilerPlugin = new CompilerTimePlugin(this);
	
	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}
	
	/**
	 * 
	 */
	public TimePlugin() {
		super();
		dependencyList = new HashSet<String>();
		dependencyList.add("NumberPlugin");
	}

	/**
	 * Creates necessary functions.
	 * 
	 * @see org.coreasim.engine.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {
		nowFunction = new NowFunctionElement();
	}

	/**
	 * Returns the list of functions provided by this plugin.
	 */
	public Map<String,FunctionElement> getFunctions() {
		if (functions == null) {
			functions = new HashMap<String,FunctionElement>();
			functions.put(NowFunctionElement.NOW_FUNC_NAME, nowFunction);
			functions.put(StepCountFunctionElement.FUNC_NAME, new StepCountFunctionElement(capi));
		}
		return functions;
	}

	/**
	 * @return <code>null</code>
	 */
	public Map<String,UniverseElement> getUniverses() {
		return Collections.emptyMap();
	}

	public Set<String> getRuleNames() {
		return Collections.emptySet();
	}

	public Map<String, RuleElement> getRules() {
		return null;
	}

	/**
	 * @return <code>null</code>
	 */
	public Map<String,BackgroundElement> getBackgrounds() {
		return Collections.emptyMap();
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.plugin.Plugin#getDependencyNames()
	 */
	@Override
	public Set<String> getDependencyNames() {
		return this.dependencyList;
	}

	public Set<String> getBackgroundNames() {
		return Collections.emptySet();	}

	public Set<String> getFunctionNames() {
		return getFunctions().keySet();
	}

	public Set<String> getUniverseNames() {
		return Collections.emptySet();
	}

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
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
