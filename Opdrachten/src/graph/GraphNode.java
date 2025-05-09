package graph;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {
	private final String id;
	private final List<String> metadata;

	public GraphNode(String id) {
		this.id = id;
		this.metadata = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public List<String> getMetadata() {
		return metadata;
	}

	public void addMetadata(String data) {
		metadata.add(data);
	}
}
