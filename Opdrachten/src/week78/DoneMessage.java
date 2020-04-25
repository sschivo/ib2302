package week78;

import framework.Message;

public class DoneMessage implements Message {
	
	public DoneMessage() {};

	@Override
	public String toString() {
		return "<done>";
	}
}
