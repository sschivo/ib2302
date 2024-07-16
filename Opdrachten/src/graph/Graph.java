package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a graph. (used for visualization)
 */
public class Graph {
	private final List<GraphNode> nodes;
	private final List<GraphEdge> edges;

	public Graph() {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
	}

	public void addNode(GraphNode node) {
		nodes.add(node);
	}

	public void addEdge(GraphEdge edge) {
		edges.add(edge);
	}

	public List<GraphNode> getNodes() {
		return nodes;
	}

	public List<GraphEdge> getEdges() {
		return edges;
	}
}

