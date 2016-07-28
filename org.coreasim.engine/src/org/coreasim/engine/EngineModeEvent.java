/*	
 * EngineModeEvent.java 	1.0 	$Revision: 243 $
 * 
 *
 * Copyright (C) 2006 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine;

/** 
 * Engine mode change event
 *   
 *  @author  Roozbeh Farahbod
 *  
 */
public class EngineModeEvent extends EngineEvent {
	
	/** Old mode of the engine */
	protected final CoreASIMEngine.EngineMode oldMode;
	
	/** New mode of the engine */
	protected final CoreASIMEngine.EngineMode newMode;

	/**
	 * Creates a new engine mode event with the given old and new modes.
	 */
	public EngineModeEvent(CoreASIMEngine.EngineMode oldMode,
			CoreASIMEngine.EngineMode newMode) {
		this.oldMode = oldMode;
		this.newMode = newMode;
	}

	/**
	 * @return Returns the newMode.
	 */
	public CoreASIMEngine.EngineMode getNewMode() {
		return newMode;
	}

	/**
	 * @return Returns the oldMode.
	 */
	public CoreASIMEngine.EngineMode getOldMode() {
		return oldMode;
	}

}
