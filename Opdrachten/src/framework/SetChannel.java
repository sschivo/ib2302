package framework;

import java.util.Collection;
import java.util.HashSet;

public class SetChannel extends Channel {

	public SetChannel(Process sender, Process receiver) {
		super(sender, receiver);
	}

	@Override
	public Collection<Message> newBuffer() {
		return new HashSet<>();
	}
}
