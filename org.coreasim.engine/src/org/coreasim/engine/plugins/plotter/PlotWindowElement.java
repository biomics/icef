/*	
 * PlotWindowElement.java 	1.0 
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
 
package org.coreasim.engine.plugins.plotter;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.FunctionElement;

/** 
 * This is the Plot Window element in the CoreASM state. 
 * This element can be used through the 'plot' rule to 
 * plot various functions in one window.
 *   
 * @author  Roozbeh Farahbod
 * 
 */
public class PlotWindowElement extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8303895329792360725L;
	/* reference to a Plot Window frame */
	private final PlotWindow window;
	
	public PlotWindowElement() {
		window = new PlotWindow();
	}

	public String getBackground() {
		return PlotWindowBackground.NAME;
	}
	
	/**
	 * Adds another function to the plot window 
	 */
	public void addFunction(FunctionElement f, String name) {
		window.addFunction(f, name);
	}
	
	/**
	 * Sets the visibility of its plot window
	 * 
	 * @see PlotWindow#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		window.setVisible(b);
		if (b) 
			window.repaint();
	}

	/**
	 * Sends a kill signal to its plot window.
	 * 
	 * @see PlotWindow#setKilled(boolean)
	 *
	 */
	public void killWindow() {
		window.setKilled(true);
	}

}
