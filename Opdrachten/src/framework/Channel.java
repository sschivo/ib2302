package framework;

import java.util.Collection;
import java.util.Iterator;

public abstract class Channel {
	
	private Process sender;
	private Process receiver;
	private Collection<Message> buffer;
	
	public Channel(Process sender, Process receiver) {
		this.sender = sender;
		this.receiver = receiver;
		this.buffer = newBuffer();
		sender.addOutgoing(this);
		receiver.addIncoming(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Channel) {
			Channel c = (Channel) obj;
			return sender.equals(c.sender) && receiver.equals(c.receiver);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return sender.hashCode() + receiver.hashCode();
	}
	
	@Override
	public String toString() {
		return sender.getName() + "->" + receiver.getName();
	}
	
	public Collection<Message> getContent() {
		Collection<Message> content = newBuffer();
		content.addAll(buffer);
		return content;
	}
	
	public Process getSender() {
		return sender;
	}
	
	public Process getReceiver() {
		return receiver;
	}
	
	void put(Message m) {
		buffer.add(m);
	}
	
	Message take() {
		if (buffer.isEmpty()) {
			throw new IllegalStateException();
		}
		
		Iterator<Message> i = buffer.iterator();
		Message m = i.next();
		i.remove();
		return m;
	}
	
	abstract Collection<Message> newBuffer();
}
