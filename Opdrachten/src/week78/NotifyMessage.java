package week78;

import framework.Message;

public class NotifyMessage implements Message {
	
	public NotifyMessage() {};

	@Override
	public String toString() {
		return "<notify>";
	}
}
