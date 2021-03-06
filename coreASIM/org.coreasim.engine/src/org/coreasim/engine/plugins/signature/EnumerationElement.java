/*	
 * EnumerationElement.java 	1.0 	
 * 
 * Copyright (C) 2006 George Ma
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
 
package org.coreasim.engine.plugins.signature;

import org.coreasim.engine.absstorage.Element;

/** 
 *	Class for elements of Enumeration backgrounds
 *   
 *  @author  George Ma, Roozbeh Farahbod
 *  
 */
public class EnumerationElement extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8601292219146912662L;

	/** name of this element */
    private String name;
    
    private String backgroundName = null;
    
    public EnumerationElement(String name) {
        super();
        this.name = name;
    }

    /**
     * @return the name of the element
     */
    public String getName() {
        return name;
    }
    
    public String toString() {
    	return name;
    }
    
    public String getBackground() {
    	if (backgroundName == null)
    		return super.getBackground();
    	else
    		return backgroundName;
    }
    
    /*
     * The signature plugin can set the name of 
     * the background of this element.
     */
    protected void setBackground(String name) {
    	this.backgroundName = name;
    }
}
