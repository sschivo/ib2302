package week56;

import framework.Message;

public class InfoMessage implements Message {
	
	public InfoMessage() {};

	@Override
	public String toString() {
		return "<info>";
	}
}
