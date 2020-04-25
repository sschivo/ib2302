package week34;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import framework.Channel;
import framework.Message;
import framework.Process;

public abstract class SnapshotProcess extends Process {
	
	private boolean started = false;
	private boolean finished = false;
	private Map<Channel, List<Message>> channelStates = new LinkedHashMap<>();

	public void startSnapshot() {
		if (started || finished) {
			throw new IllegalStateException();
		}
		
		started = true;
	}
	
	public void finishSnapshot() {
		if (!started || finished) {
			throw new IllegalStateException();
		}
		
		finished = true;
	}

	public void record(Channel c, List<Message> messages) {
		if (!channelStates.containsKey(c)) {
			channelStates.put(c, new ArrayList<>());
		}
		
		channelStates.get(c).addAll(messages);
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean hasFinished() {
		return finished;
	}
	
	public List<Message> getChannelState(Channel c) {
		if (!channelStates.containsKey(c)) {
			channelStates.put(c, new ArrayList<>());
		}
		
		return new ArrayList<>(channelStates.get(c));
	}
}
