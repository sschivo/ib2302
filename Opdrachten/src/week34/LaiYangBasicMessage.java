package week34;

import framework.Message;

public class LaiYangBasicMessage implements Message {

	private boolean tag;
	private String content;

	public LaiYangBasicMessage(String content, boolean tag) {
		this.content = content;
		this.tag = tag;
	}

	public boolean getTag() {
		return tag;
	}

	public String getContent() {
		return content;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LaiYangBasicMessage) {
			return toString().equals(((LaiYangBasicMessage) obj).toString());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "<" + content + ", " + tag + ">";
	}
}
