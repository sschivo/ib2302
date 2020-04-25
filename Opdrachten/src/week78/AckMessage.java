package week78;

import framework.Message;

public class AckMessage implements Message {
	
	public AckMessage() {};

	@Override
	public String toString() {
		return "<ack>";
	}
}
