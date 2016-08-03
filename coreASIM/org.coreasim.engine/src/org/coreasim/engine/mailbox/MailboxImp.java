/*
 * MailboxImp		1.0
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */
package org.coreasim.engine.mailbox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.Engine;
import org.coreasim.engine.absstorage.AbstractStorage;
import org.coreasim.engine.absstorage.FunctionElement;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.absstorage.UnmodifiableFunctionException;
import org.coreasim.engine.plugin.PluginServiceInterface;
import org.coreasim.engine.plugins.communication.CommunicationPlugin;
import org.coreasim.engine.plugins.communication.CommunicationPlugin.CommunicationPSI;

public class MailboxImp implements Mailbox {
	/** 
	 * Concrete implementation of the mailbox
	 *   
	 *  @author Eric Rothstein
	 *  @author Daniel Schreckling
	 *  
	 */
	private Set<MessageElement> inbox;
	private Set<MessageElement> outbox;
	private ControlAPI capi;
	private CommunicationPSI communicationPSI;
	private Set<MessageElement> schedulingOutbox;
	private int timesExecuted =-1;

	@Override
	public synchronized Set<MessageElement> emptyOutbox() {
        /* for(MessageElement e : outbox) {
            System.out.println("[Thread "+Thread.currentThread().getName()+"]: OUTBOX EMPTY> "+e);
            } */

        Set<MessageElement> oldOutbox = null;
        oldOutbox = new HashSet<MessageElement>();
        for(MessageElement me : outbox) {
            oldOutbox.add(new MessageElement(me));
        }
        for(MessageElement me : schedulingOutbox) {
            oldOutbox.add(new MessageElement(me));
        }
        schedulingOutbox.clear();
        outbox.clear();
		return oldOutbox;
	}

	@Override
	public void fillInbox(Set<MessageElement> msgs) {
        synchronized(inbox) {
            inbox.addAll(msgs);
        }
	}

	@Override
	public Set<MessageElement> getInbox() {
        synchronized(inbox) {
            return inbox;
        }
	}

	@Override
	public synchronized void putOnSchedulingOutbox(MessageElement message) {
		schedulingOutbox.add(message);
	}

	@Override
	public void prepareInitialState() {
		communicationPSI = (CommunicationPSI) capi.getPluginInterface(CommunicationPlugin.PLUGIN_NAME);
		inbox.clear();
		outbox.clear();

	}
/**
 * initializes the Mailbox
 * @param capi
 */
	public MailboxImp(ControlAPI capi) {
		super();
		inbox = new HashSet<MessageElement>();
		outbox = new HashSet<MessageElement>();
		schedulingOutbox = new HashSet<MessageElement>();
		this.capi = capi;
		}

	@Override
	public void startStep() {
		if(!outbox.isEmpty())
			capi.error("Outbox is not empty at the beginning of the current step");
        synchronized(inbox) {
            communicationPSI.updateInboxLocation(inbox);
            inbox.clear();
        }
		outbox.clear();
	}

	@Override
	public synchronized void endStep() {
		timesExecuted++;
		if(!inbox.isEmpty())
			capi.error("Inbox is not empty at the end of the current step");

			outbox.addAll(communicationPSI.collectOutgoingMessages());
			outbox.addAll(schedulingOutbox);
	        /* for(MessageElement e : outbox) {
               System.out.println("[Thread "+Thread.currentThread().getName()+"]: OUTBOX ADD> "+e+"; endStep:" +timesExecuted +" times. StartingStep: "+capi.getCounter()+". Stepcount: "+capi.getStepCount());
               }
            */
	}

	@Override
	public void loopback() {
		inbox.clear();
		inbox.addAll(outbox);
		emptyOutbox();//outbox.clear();
	}

	@Override
	public void clearOutboxLocation() {
		communicationPSI.clearOutboxLocation();
	}

}
