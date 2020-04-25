package framework;

import java.util.ArrayList;
import java.util.Collection;

public class ListChannel extends Channel {

	public ListChannel(Process sender, Process receiver) {
		super(sender, receiver);
	}

	@Override
	public Collection<Message> newBuffer() {
		return new ArrayList<>();
	}
}
