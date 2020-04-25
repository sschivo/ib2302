package week34;

import framework.Message;

public class LaiYangControlMessage implements Message {

	private int n;

	public LaiYangControlMessage(int n) {
		this.n = n;
	}

	public int getN() {
		return n;
	}

	@Override
	public String toString() {
		return String.valueOf(n);
	}
}
