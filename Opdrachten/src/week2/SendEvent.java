package week2;

import java.util.List;

import framework.Channel;
import framework.Message;
import framework.Process;

public class SendEvent extends Event {

	private Channel c;
	private Message m;

	public SendEvent(Channel c, Message m) {
		this.c = c;
		this.m = m;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SendEvent) {
			SendEvent that = (SendEvent) o;
			return this.c.equals(that.c) && this.m.equals(that.m);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return c.hashCode();
	}
	
	@Override
	public String toString() {
		return "s(" + c.getSender() + "," + c.getReceiver() + "," + m + ")"; 
	}

	public boolean correspondsWith(ReceiveEvent r) {
		return c.equals(r.getChannel()) && m.equals(r.getMessage());
	}

	public ReceiveEvent getCorrespondingReceiveEvent(List<Event> sequence) {
		for (Event e : sequence) {
			if (e instanceof ReceiveEvent && correspondsWith((ReceiveEvent) e)) {
				return (ReceiveEvent) e;
			}
		}

		throw new IllegalArgumentException();
	}

	public Channel getChannel() {
		return c;
	}

	public Message getMessage() {
		return m;
	}
	
	@Override
	public Process getProcess() {
		return c.getSender();
	}
}
