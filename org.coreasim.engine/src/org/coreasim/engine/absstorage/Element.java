/*	
 * Element.java 	1.0 	$Revision: 243 $
 * 
 *
 * Copyright (C) 2005 Roozbeh Farahbod 
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
 *
 */
 
 
package org.coreasim.engine.absstorage;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.coreasim.engine.ControlAPI;

/** 
 *	The root class of Abstract Object Values (later changed 
 *  to Elements) in the abstract storage.
 *  
 *  @author  Roozbeh Farahbod
 *  
 */
 public class Element implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -123884716277937711L;

	/**
 	 * This value is used to automatically generate
 	 * general Element names. 
 	 */
 	private static long lastElementNo = 1; 
	 
 	/**
 	 * Represents the 'undef' value in ASM.
 	 */
 	public static final Element UNDEF = new Element(-1);
 	
 	/**
 	 * A unique id
 	 */
	public final long id;
	
 	/**
 	 * A private constructor used to create UNDEF.
 	 *  
 	 */
	private Element(long id) {
 		this.id = id;
 	}
	
	/**
	 * Creates a new Element.
	 *
	 */
 	public Element() {
		lastElementNo++;
		this.id = lastElementNo;
	}
	
 	/**
 	 * Returns the name of the background of this element
 	 * (in the state). This method should be overridden 
 	 * by elements from special backgrounds.
 	 */
 	public String getBackground() {
 		return ElementBackgroundElement.ELEMENT_BACKGROUND_NAME;
 	}
 	
 	/*
 	 * Returns the class of the background of this element
 	 * (in the state). This method should be overridden 
 	 * by elements of special backgrounds.
 	 *
 	public Class<? extends BackgroundElement> getBackgroundClass() {
 		return ElementBackgroundElement.class;
 	}
 	*/
 	
 	/**
 	 * If this element has a background (see {@link BackgroundElement}),
 	 * it asks the background (through Control API) to provide a 
 	 * new instance of the elements provided by that background. 
 	 * There is no guarantee that the returned value is from the same 
 	 * type of this element. 
 	 * <p>
 	 * If the element has no background, <code>null</code> is returned.
 	 * 
 	 * @param capi reference to the Control API of the engine
 	 * 
 	 * @see BackgroundElement#getNewValue()
 	 */
 	public final Element getNewInstance(ControlAPI capi) {
 		Element result = null;
 		AbstractUniverse u = capi.getStorage().getUniverse(this.getBackground());
		if (u != null && (u instanceof BackgroundElement)) {
			BackgroundElement bkg = (BackgroundElement)u;
			result = bkg.getNewValue();
		}
		return result;
 	}
 	
 	/**
 	 * Compares this Element to the specified Element. 
 	 * The result is <code>true</code> if the argument 
 	 * is not null and is considered to be equal to this Element.
 	 * 
 	 * @param anElement the Element to compare with.
 	 * @return <code>true</code> if the Elements are equal; <code>false</code> otherwise.
 	 * @throws IllegalArgumentException if <code>anElement</code> is not an instance
 	 * of <code>Element</code>
 	 */
 	public boolean equals(Object anElement) {
		if (anElement == null)
			return false;
 		if (anElement instanceof Element)
 			return (((Element)anElement).id == this.id);
 		else
 			throw new IllegalArgumentException("Cannot compare to non-Elements.");
 	}
 	

 	/** 
 	 * Returns the denotational form of this element. 
 	 * By default, this is the same as <code>toString()</code>
 	 * but elements can override this method to provide
 	 * a more accurate denotation of their value.
 	 */
 	public String denotation() {
 		return this.toString();
 	}
 	
 	/** 
	 * Returns a <code>String</code> representation of 
	 * this Element.
	 *  
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (id == -1)
			return "undef";
		else
			return getClass().getSimpleName() + id;
	}

	    public static void main(String[] args)
	    {
	        System.out.println("Is Serializable? "+isSerializable(new Element()));
	    }

	    public static boolean isSerializable(final Object o)
	    {
	        final boolean retVal;

	        if(implementsInterface(o))
	        {
	            retVal = attemptToSerialize(o);
	        }
	        else
	        {
	            retVal = false;
	        }

	        return (retVal);
	    }

	    private static boolean implementsInterface(final Object o)
	    {
	        final boolean retVal;

	        retVal = ((o instanceof Serializable) || (o instanceof Externalizable));

	        return (retVal);
	    }

	    private static boolean attemptToSerialize(final Object o)
	    {
	        final OutputStream sink;
	        ObjectOutputStream stream;

	        stream = null;

	        try
	        {
	            sink   = new ByteArrayOutputStream();
	            stream = new ObjectOutputStream(sink);
	            stream.writeObject(o);
	            // could also re-serilalize at this point too
	        }
	        catch(final IOException ex)
	        {
	            return (false);
	        }
	        finally
	        {
	            if(stream != null)
	            {
	                try
	                {
	                    stream.close();
	                }
	                catch(final IOException ex)
	                {
	                    // should not be able to happen
	                }
	            }
	        }

	        return (true);
	    }
	}

 
