package week56;

import framework.Message;

public class TokenMessage implements Message {
	
	public TokenMessage() {};

	@Override
	public String toString() {
		return "<token>";
	}
}
