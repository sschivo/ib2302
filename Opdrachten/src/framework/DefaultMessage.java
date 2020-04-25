package framework;

public class DefaultMessage implements Message {

	private String id;
	
	public DefaultMessage(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultMessage) {
			DefaultMessage that = (DefaultMessage) o;
			return this.id.equals(that.id);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id;
	}
}
