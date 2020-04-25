package week2;

import java.util.List;

import framework.Channel;
import framework.Message;
import framework.Process;

public class ReceiveEvent extends Event {

	private Channel c;
	private Message m;

	public ReceiveEvent(Channel c, Message m) {
		this.c = c;
		this.m = m;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ReceiveEvent) {
			ReceiveEvent that = (ReceiveEvent) o;
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
		return "r(" + c.getSender() + "," + c.getReceiver() + "," + m + ")"; 
	}
	
	public boolean correspondsWith(SendEvent s) {
		return c.equals(s.getChannel()) && m.equals(s.getMessage());
	}

	public SendEvent getCorrespondingSendEvent(List<Event> sequence) {
		for (Event e : sequence) {
			if (e instanceof SendEvent && correspondsWith((SendEvent) e)) {
				return (SendEvent) e;
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
		return c.getReceiver();
	}
}
