/* 
 * Copyright (C) 2016 BIOMICS project (Eric Rothstein, Daniel Schreckling)
 * 
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.mailbox;

import java.util.Set;

import org.coreasm.engine.absstorage.MessageElement;

/** 
 * Defines the interface of the mailbox
 *   
 *  @author Eric Rothstein
 *  @author Daniel Schreckling
 *  
 */
public interface Mailbox {
	
    /**
     * Returns a the message elements in the outbox,
     * which wait for being sent. After the execution
     * of this instruction, the outbox is empty.
     *
     * @return Set of MessageElements in the outbox
     */
    public Set<MessageElement> emptyOutbox();
    
    /**
     * Puts the Set of MessageElements into the Inbox
     * of this coreASM
     *
     * @param msgs The message arriving at the mailbox which need to be stored in the Inbox
     */
    public void fillInbox(Set<MessageElement> msgs);
    
    /**
     * Returns all the message elements in the inbox
     * @return the message elements in the inbox
     */
    public Set<MessageElement> getInbox();
    	
    /**
     * Puts the message into the outbox
     * @param message The MessageElement to be put into the outbox
     */
    public void putOnOutbox(MessageElement message); 
    
    /**
     * Cleans up any cached data in the mailbox to run a new specification
     */
	public void prepareInitialState();

	/**
     * Checks if the mailbox is ready to start a step. More precisely, we
     * make sure that the outbox of the mailbox is empty, and that the inbox location
     * used by the communication plugin has the set of incoming MessageElements
     */
	public void startStep();
	
	/**
     * Checks if the mailbox is ready to end a step. More precisely, we
     * make sure that the inbox of the mailbox is empty, and that the we get the 
     * MessageElements from the outbox location used by the communication plugin.
     */
	public void endStep();
	
	/**
	 * This method is for testing purposes only. It assigns the content of the outbox
	 * to the inbox, and empties the outbox
	 */
	public void loopback();
}

