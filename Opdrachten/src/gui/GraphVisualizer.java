package gui;

import graph.Graph;
import graph.GraphNode;
import graph.GraphEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphVisualizer extends JPanel {
	private Graph graph;
	private static final int NODE_RADIUS = 30;
	private static final int PADDING = 50;
	private static final int ARROW_LENGTH = 20;
	private static final int ARROW_WIDTH = 6;
	private static final int EDGE_OFFSET = 5;
	private static final int TEXT_OFFSET = 15;
	private final Map<Shape, String> nodeTooltips;
	private final JLabel tooltipLabel;
	private Shape currentTooltipShape = null;

	public GraphVisualizer() {
		this.nodeTooltips = new LinkedHashMap<>();
		this.tooltipLabel = new JLabel();
		setLayout(null);
		tooltipLabel.setOpaque(true);
		tooltipLabel.setBackground(new Color(255, 255, 225));
		tooltipLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tooltipLabel.setVisible(false);
		add(tooltipLabel);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Check if the mouse is over a node
				for (Map.Entry<Shape, String> entry : nodeTooltips.entrySet()) {
					if (entry.getKey().contains(e.getPoint())) {
						updateTooltipContent(entry.getValue());
						setTooltipPosition(e.getPoint());
						currentTooltipShape = entry.getKey();
						tooltipLabel.setVisible(true);
						return;
					}
				}
				tooltipLabel.setVisible(false); // Hide tooltip if not over a node
				currentTooltipShape = null;
			}
		});
	}

	public void updateGraph(Graph newGraph) {
		this.graph = newGraph;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (graph == null) {
			return;
		}
		// Set anti-aliasing for better graphics
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Calculate positions of nodes
		Map<String, Point> positions = calculatePositions(graph);

		// Draw nodes
		nodeTooltips.clear(); // Clear previous tooltips
		for (GraphNode node : graph.getNodes()) {
			Point pos = positions.get(node.getId());
			int x = pos.x - NODE_RADIUS / 2;
			int y = pos.y - NODE_RADIUS / 2;

			g2.setColor(Color.BLACK);
			g2.drawOval(x, y, NODE_RADIUS, NODE_RADIUS);

			// Center the text in the node
			FontMetrics fm = g2.getFontMetrics();
			int textWidth = fm.stringWidth(node.getId());
			int textHeight = fm.getAscent();
			g2.drawString(node.getId(), x + (NODE_RADIUS - textWidth) / 2, y + (NODE_RADIUS + textHeight) / 2);

			// Create a tooltip for the node
			String tooltipText = String.join("\n", node.getMetadata());
			Ellipse2D nodeShape = new Ellipse2D.Double(x, y, NODE_RADIUS, NODE_RADIUS);
			nodeTooltips.put(nodeShape, tooltipText);
		}

		// Draw edges with arrows and metadata
		for (GraphEdge edge : graph.getEdges()) {
			Point sourcePos = positions.get(edge.getSource().getId());
			Point targetPos = positions.get(edge.getTarget().getId());
			drawArrow(g2, sourcePos, targetPos);

			// Draw metadata
			drawMetadata(g2, edge, sourcePos, targetPos);
		}

		// Update tooltip if it is visible
		if (tooltipLabel.isVisible()) {
			for (Map.Entry<Shape, String> entry : nodeTooltips.entrySet()) {
				if (entry.getKey().equals(currentTooltipShape)) {
					updateTooltipContent(entry.getValue());
					break;
				}
			}
		}
	}

	private void setTooltipPosition(Point position) {
		int x = position.x;
		int y = position.y;

		int tooltipWidth = tooltipLabel.getWidth();
		int tooltipHeight = tooltipLabel.getHeight();

		// Calculate new positions ensuring the tooltip stays within the panel
		int newX = x + 10;
		int newY = y + 10;

		if (newX + tooltipWidth > getWidth()) {
			newX = getWidth() - tooltipWidth;
		}
		if (newY + tooltipHeight > getHeight()) {
			newY = getHeight() - tooltipHeight;
		}

		tooltipLabel.setLocation(newX, newY);
	}

	private void updateTooltipContent(String text) {
		tooltipLabel.setText("<html>" + text.replaceAll("\n", "<br>") + "</html>");
		tooltipLabel.setSize(tooltipLabel.getPreferredSize());
	}

	private Map<String, Point> calculatePositions(Graph graph) {
		Map<String, Point> positions = new LinkedHashMap<>();
		int totalNodes = graph.getNodes().size();
		double angleIncrement = 2 * Math.PI / totalNodes;
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		int radius = Math.min(getWidth(), getHeight()) / 2 - PADDING;

		int index = 0;
		for (GraphNode node : graph.getNodes()) {
			double angle = index * angleIncrement;
			int x = centerX + (int) (radius * Math.cos(angle));
			int y = centerY + (int) (radius * Math.sin(angle));
			positions.put(node.getId(), new Point(x, y));
			index++;
		}

		return positions;
	}

	private void drawArrow(Graphics2D g2, Point source, Point target) {
		double dx = target.x - source.x, dy = target.y - source.y;
		double angle = Math.atan2(dy, dx);
		double distance = Math.sqrt(dx * dx + dy * dy);

		double offsetX = -Math.sin(angle) * EDGE_OFFSET;
		double offsetY = Math.cos(angle) * EDGE_OFFSET;

		double arrowStart = (double) NODE_RADIUS / 2;
		double arrowEnd = distance - (double) NODE_RADIUS / 2;

		double startX = source.x + arrowStart * Math.cos(angle) + offsetX;
		double startY = source.y + arrowStart * Math.sin(angle) + offsetY;
		double endX = source.x + arrowEnd * Math.cos(angle) + offsetX;
		double endY = source.y + arrowEnd * Math.sin(angle) + offsetY;

		AffineTransform at = AffineTransform.getTranslateInstance(startX, startY);
		at.concatenate(AffineTransform.getRotateInstance(angle));

		// Save the current transform
		AffineTransform oldTransform = g2.getTransform();

		g2.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		int len = (int) (arrowEnd - arrowStart);
		g2.drawLine(0, 0, len, 0);
		g2.fillPolygon(new int[]{len, len - ARROW_LENGTH, len - ARROW_LENGTH, len},
				new int[]{0, -ARROW_WIDTH, ARROW_WIDTH, 0}, 4);

		// Reset the transform to the saved one
		g2.setTransform(oldTransform);
	}

	private void drawMetadata(Graphics2D g2, GraphEdge edge, Point source, Point target) {
		double dx = target.x - source.x, dy = target.y - source.y;
		double angle = Math.atan2(dy, dx);

		double offsetX = -Math.sin(angle) * TEXT_OFFSET;
		double offsetY = Math.cos(angle) * TEXT_OFFSET;

		// Position the text closer to the target point
		int metaX = (int) ((source.x * 0.25 + target.x * 0.75) + offsetX);
		int metaY = (int) ((source.y * 0.25 + target.y * 0.75) + offsetY);

		// Draw each metadata string, applying colors if specified
		Color originalColor = g2.getColor();
		for (String meta : edge.getMetadata()) {
			String message = meta;
			if (meta.startsWith("[color]")) {
				// Extract color and message
				String[] parts = meta.split(" ", 3);
				if (parts.length == 3) {
					String colorHex = parts[1];
					message = parts[2];

					// Set color
					g2.setColor(Color.decode(colorHex));
				}
			} else {
				// Reset color to original if no color specified
				g2.setColor(originalColor);
			}

			// Draw message
			g2.drawString(message, metaX, metaY);
			metaX += g2.getFontMetrics().stringWidth(message);

			// Draw a comma if this is not the last metadata
			if (!meta.equals(edge.getMetadata().get(edge.getMetadata().size() - 1))) {
				g2.setColor(originalColor); // Ensure comma is drawn in original color
				g2.drawString(", ", metaX, metaY);
				metaX += g2.getFontMetrics().stringWidth(", ");
			}
		}
		g2.setColor(originalColor); // Reset color to original
	}
}

