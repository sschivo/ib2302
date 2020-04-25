package framework;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class Process {
	
	private String name;
	private Set<Channel> incoming = new LinkedHashSet<>();
	private Set<Channel> outgoing = new LinkedHashSet<>();
	List<String> printed = new ArrayList<>();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Process) {
			Process p = (Process) obj;
			return name.equals(p.name);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name + ":" + getClass().getName();
	}
	
	void addIncoming(Channel c) {
		incoming.add(c);
	}
	
	void addOutgoing(Channel c) {
		outgoing.add(c);
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	public Set<Channel> getIncoming() {
		return new LinkedHashSet<>(incoming);
	}
	
	public Set<Channel> getOutgoing() {
		return new LinkedHashSet<>(outgoing);
	}
	
	public String getName() {
		return name;
	}
	
	public void print(String s) {
		System.out.println(name + ": " + s);
		printed.add(s);
	}
	
	public void send(Message m, Channel c) {
		if (!outgoing.contains(c)) {
			throw new IllegalArgumentException();
		}
		
		c.put(m);
	}
	
	public abstract void init();
	
	public abstract void receive(Message m, Channel c) throws IllegalReceiveException;
}
