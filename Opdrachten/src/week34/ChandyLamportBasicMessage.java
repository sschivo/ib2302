package week34;

import framework.Message;

public class ChandyLamportBasicMessage implements Message {

	private String content;

	public ChandyLamportBasicMessage(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChandyLamportBasicMessage) {
			return content.equals(((ChandyLamportBasicMessage) obj).getContent());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return content.hashCode();
	}

	@Override
	public String toString() {
		return content;
	}
}
