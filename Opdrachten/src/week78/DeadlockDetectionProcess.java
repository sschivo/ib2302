package week78;

import java.util.Set;
import java.util.HashSet;

import framework.Channel;
import framework.Process;

public abstract class DeadlockDetectionProcess extends Process {

	// Sets of incoming and outgoing edges of the process' local wait-for graph
	protected Set<Channel> inRequests = new HashSet<Channel>();
	protected Set<Channel> outRequests = new HashSet<Channel>();

	// Number of outstanding requests
	protected int requests;

	void setRequests(int requests) {
		this.requests = requests;
	}

	void addInRequest(Channel c) {
		inRequests.add(c);
	}

	void addOutRequest(Channel c) {
		outRequests.add(c);
	}
}
