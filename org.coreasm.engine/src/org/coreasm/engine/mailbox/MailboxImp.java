package org.coreasm.engine.mailbox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.coreasm.engine.absstorage.MessageElement;

public class MailboxImp implements Mailbox {

	private Set<MessageElement> inbox;
	private Set<MessageElement> outbox;

	@Override
	public Set<MessageElement> emptyOutbox() {
		Set<MessageElement> oldOutbox = Collections.unmodifiableSet(outbox);
		outbox.clear();
		return oldOutbox;
	}

	@Override
	public Set<MessageElement> fillInbox(Set<MessageElement> msgs) {
		Set<MessageElement> oldInbox = Collections.unmodifiableSet(inbox);
		inbox = msgs;
		return oldInbox;
	}

	@Override
	public Set<MessageElement> getInbox() {
		return Collections.unmodifiableSet(inbox);
	}

	@Override
	public void putOnOutbox(MessageElement message) {
		outbox.add(message);
	}

	@Override
	public void cleanUp() {
		inbox.clear();
		outbox.clear();

	}

	@Override
	public void dispose() {
		inbox.clear();
		outbox.clear();
	}

	public MailboxImp() {
		super();
		inbox = new HashSet<MessageElement>();
		outbox = new HashSet<MessageElement>();
	}

}
