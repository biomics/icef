package org.coreasm.engine.mailbox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.coreasm.engine.ControlAPI;
import org.coreasm.engine.Engine;
import org.coreasm.engine.absstorage.AbstractStorage;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.absstorage.MessageElement;
import org.coreasm.engine.absstorage.UnmodifiableFunctionException;
import org.coreasm.engine.plugin.PluginServiceInterface;
import org.coreasm.engine.plugins.communication.CommunicationPlugin;
import org.coreasm.engine.plugins.communication.CommunicationPlugin.CommunicationPSI;

public class MailboxImp implements Mailbox {

	private Set<MessageElement> inbox;
	private Set<MessageElement> outbox;
	private ControlAPI capi;
	private CommunicationPSI communicationPSI;
	private Set<MessageElement> schedulingOutbox;
	private int timesExecuted =-1;

	@Override
	public synchronized Set<MessageElement> emptyOutbox() {
        for(MessageElement e : outbox) {
            System.out.println("[Thread "+Thread.currentThread().getName()+"]: OUTBOX EMPTY> "+e);
        }

        Set<MessageElement> oldOutbox = null;
        oldOutbox = new HashSet<MessageElement>();
        for(MessageElement me : outbox) {
            oldOutbox.add(new MessageElement(me));
        }
        schedulingOutbox.clear();
        outbox.clear();
		return oldOutbox;
	}

	@Override
	public synchronized void fillInbox(Set<MessageElement> msgs) {
        inbox.addAll(msgs);
	}

	@Override
	public synchronized Set<MessageElement> getInbox() {
		return inbox;
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

	public MailboxImp(ControlAPI capi) {
		super();
		inbox = new HashSet<MessageElement>();
		outbox = new HashSet<MessageElement>();
		schedulingOutbox = new HashSet<MessageElement>();
		this.capi = capi;
		}

	@Override
	public synchronized void startStep() {
		if(!outbox.isEmpty())
			capi.error("Outbox is not empty at the beginning of the current step");
		communicationPSI.updateInboxLocation(inbox);
		inbox.clear();
		outbox.clear();
		
	}

	@Override
	public synchronized void endStep() {
		timesExecuted++;
		if(!inbox.isEmpty())
			capi.error("Inbox is not empty at the end of the current step");
		synchronized(communicationPSI.collectOutgoingMessages()){
			int sizeOutbox = communicationPSI.collectOutgoingMessages().size();
			outbox.addAll(communicationPSI.collectOutgoingMessages());
			outbox.addAll(schedulingOutbox);
	        for(MessageElement e : outbox) {
	            System.out.println("[Thread "+Thread.currentThread().getName()+"]: OUTBOX ADD> "+e+" Size of Agent Outbox: "+sizeOutbox+". endStep:" +timesExecuted +" times. StartingStep: "+capi.getCounter()+". Stepcount: "+capi.getStepCount());
	        }
		}
	}

	@Override
	public void loopback() {
		inbox.clear();
		inbox.addAll(outbox);
		emptyOutbox();//outbox.clear();
	}

	@Override
	public synchronized void clearOutboxLocation() {
		communicationPSI.clearOutboxLocation();
	}

}
