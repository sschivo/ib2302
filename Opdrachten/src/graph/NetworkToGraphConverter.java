package graph;

import framework.Channel;
import framework.Message;
import framework.Network;
import framework.Process;

import java.util.Map;

/**
 * Converts a network of processes to a graph for visualization.
 */
public class NetworkToGraphConverter {

	/**
	 * Converts a network to a graph.
	 *
	 * @param network             the network to convert
	 * @param showOnlyActiveEdges if true, only channels with messages are shown
	 * @param nextChannel         the next channel to be processed
	 * @param nextMessage         the next message to be processed
	 *
	 * @return the converted graph
	 */
	public static Graph convert(Network network, boolean showOnlyActiveEdges, Channel nextChannel, Message nextMessage) {
		Graph graph = new Graph();

		// Add nodes
		for (Process p : network.getProcesses().values()) {
			GraphNode node = new GraphNode(p.getName());
			// Add metadata from the process as metadata for the node
			node.getMetadata().addAll(p.getMetadata());
			graph.addNode(node);
		}

		// Add edges
		for (Map.Entry<String, Map<String, Channel>> entry : network.getChannels().entrySet()) {
			String senderName = entry.getKey();
			GraphNode senderNode = graph.getNodes().stream().filter(n -> n.getId().equals(senderName)).findFirst().get();

			for (Map.Entry<String, Channel> channelEntry : entry.getValue().entrySet()) {
				Channel channel = channelEntry.getValue();

				// Check if the channel has messages if showOnlyActiveEdges is true
				if (showOnlyActiveEdges && channel.getContent().isEmpty()) {
					continue;
				}

				String receiverName = channelEntry.getKey();
				GraphNode receiverNode = graph.getNodes().stream().filter(n -> n.getId().equals(receiverName)).findFirst().get();
				GraphEdge edge = new GraphEdge(senderNode, receiverNode);

				// Add messages from the channel as metadata
				for (Message message : channel.getContent()) {
					String metaString = message.toString();
					if (channel.equals(nextChannel) && message.equals(nextMessage)) {
						// Highlight the next message to be processed in red
						metaString = "[color] #FF0000 " + metaString;
					}
					edge.addMetadata(metaString);
				}

				graph.addEdge(edge);
			}
		}

		return graph;
	}

	public static Graph convert(Network network, boolean showOnlyActiveEdges) {
		return convert(network, showOnlyActiveEdges, null, null);
	}
}
