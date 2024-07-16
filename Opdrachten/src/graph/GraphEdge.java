package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a graph edge.
 */
public class GraphEdge {
	private final GraphNode source;
	private final GraphNode target;
	// Metadata is additional information about the edge
	private final List<String> metadata;

	public GraphEdge(GraphNode source, GraphNode target) {
		this.source = source;
		this.target = target;
		this.metadata = new ArrayList<>();
	}

	public GraphNode getSource() {
		return source;
	}

	public GraphNode getTarget() {
		return target;
	}

	public List<String> getMetadata() {
		return metadata;
	}

	public void addMetadata(String data) {
		metadata.add(data);
	}
}
