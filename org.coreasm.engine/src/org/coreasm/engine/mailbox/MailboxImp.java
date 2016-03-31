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

	@Override
	public Set<MessageElement> emptyOutbox() {
		Set<MessageElement> oldOutbox = new HashSet<MessageElement>(outbox);
		outbox.clear();
		return oldOutbox;
	}

	@Override
	public void fillInbox(Set<MessageElement> msgs) {
		//Does the inbox have to be empty as precondition?
		inbox.addAll(msgs);
	}

	@Override
	public Set<MessageElement> getInbox() {
		return inbox;
	}

	@Override
	public void putOnOutbox(MessageElement message) {
		outbox.add(message);
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
		this.capi = capi;
		}

	@Override
	public void startStep() {
		if(!outbox.isEmpty())
			capi.error("Outbox is not empty at the beginning of the current step");
		communicationPSI.updateInboxLocation(inbox);
		inbox.clear();
	}

	@Override
	public void endStep() {
		if(!inbox.isEmpty())
			capi.error("Inbox is not empty at the end of the current step");
		outbox.clear();
		outbox.addAll(communicationPSI.collectOutgoingMessages());	
	}

	@Override
	public void loopback() {
		inbox.clear();
		inbox.addAll(outbox);
		emptyOutbox();//outbox.clear();
	}

}
