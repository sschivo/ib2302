package gui;

import facade.SimulationController;

import javax.swing.*;
import java.awt.*;

public class NetworkVisualizerFrame extends JFrame {
	private final SimulationController controller;
	private final GraphVisualizer graphVisualizer;
	private final SimulationControlPanel controlPanel;
	private final SimulationStatusPanel statusPanel;
	private SimulationController.UIState uiState;

	public NetworkVisualizerFrame(SimulationController controller, GraphVisualizer graphVisualizer, SimulationControlPanel controlPanel, SimulationStatusPanel statusPanel) {
		this.controller = controller;
		this.graphVisualizer = graphVisualizer;
		this.controlPanel = controlPanel;
		this.statusPanel = statusPanel;
		this.uiState = controller.getUIState();

		// subscribe to the controller
		controller.consume((uiState) -> {
			this.uiState = uiState;
			SwingUtilities.invokeLater(() -> graphVisualizer.updateGraph(uiState.graph));
		});

		// Set up the frame
		setTitle("Network Visualizer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		add(graphVisualizer, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
		add(statusPanel, BorderLayout.NORTH);

		setSize(800, 800);
		setLocationRelativeTo(null); // Center the frame on the screen
		setVisible(true);

		graphVisualizer.updateGraph(uiState.graph); // Initial update to display the graph
	}

	public void setShowOnlyActiveEdges(boolean showOnlyActiveEdges) {
		controller.setShowOnlyActiveEdges(showOnlyActiveEdges);
	}
}
