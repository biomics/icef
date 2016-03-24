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
 *	Defines the interface of the mailbox
 *   
 *  @author Eric Rothstein
 *  @author Daniel Schreckling
 *  
 */
public interface Mailbox {

    /**
	 * Returns a the message elements in the outbox,
     * which wait for beeing sent. After the execution
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
	 * @return Set of MessageElements which already existed in the Inbox
	 */
	public Set<MessageElement> fillInbox(Set<MessageElement> msgs);

	/**
	 * Returns a the message elements in the inbox
	 * @return the message elements in the inbox
	 */
	public Set<MessageElement> getInbox();
	
	/**
	 * Returns a the message elements in the inbox
	 * @return the message elements in the inbox
	 */
	public void sendMessage(MessageElement message);
	
	/**
	 * Returns an instance of this mailbox registered
	 * for the running thread.
	 */
	public Mailbox getMailboxInstance();
	
    
    /**
     * Cleans up any cached data in the mailbox.
     * 
     */
    public void cleanUp();
    
    public void dispose();
}
