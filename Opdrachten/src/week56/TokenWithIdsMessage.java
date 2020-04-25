package week56;

import java.util.Set;
import java.util.HashSet;

public class TokenWithIdsMessage extends TokenMessage {
	
	private Set<String> ids;

	public TokenWithIdsMessage(String id) {
		ids = new HashSet<String>();
		ids.add(id);
	};

	public void addId(String id) {
		ids.add(id);
	}

	public Set<String> getIds() {
		return ids;
	}

	@Override
	public String toString() {
		return "<token, " + ids.toString() + ">";
	}
}
