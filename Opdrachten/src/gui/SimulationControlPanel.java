package gui;

import facade.SimulationController;
import framework.SimulationState;

import javax.swing.*;
import java.awt.*;

public class SimulationControlPanel extends JPanel {
	private final SimulationController controller;
	private final JButton playPauseButton;
	private final JButton stepForwardButton;
	private final JButton stepBackButton;
	private final JButton resetButton;
	private final JTextField stepTextField;
	private final JButton jumpToStepButton;
	private SimulationController.UIState uiState;

	public SimulationControlPanel(SimulationController controller) {
		this.controller = controller;
		this.playPauseButton = new JButton("Play");
		this.stepForwardButton = new JButton("Step +");
		this.stepBackButton = new JButton("Step -");
		this.resetButton = new JButton("Reset");
		this.stepTextField = new JTextField(5);
		this.jumpToStepButton = new JButton("Jump to Step");

		// initialize the UI state
		uiState = controller.getUIState();

		// subscribe to the controller
		controller.consume(this::update);

		setLayout(new FlowLayout());

		playPauseButton.addActionListener(e -> {
			SimulationState state = uiState.state;
			if (state == SimulationState.RUNNING) {
				controller.pauseSimulation();
			} else if (state == SimulationState.PAUSED || state == SimulationState.IDLE) {
				controller.resumeSimulation();
			}
		});

		stepForwardButton.addActionListener(e -> controller.stepSimulation());

		stepBackButton.addActionListener(e -> controller.stepBackSimulation());

		resetButton.addActionListener(e -> controller.resetSimulation());

		jumpToStepButton.addActionListener(e -> {
			try {
				int step = Integer.parseInt(stepTextField.getText());
				controller.jumpToStep(step);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Please enter a valid step number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			}
		});

		add(playPauseButton);
		add(stepForwardButton);
		add(stepBackButton);
		add(resetButton);
		add(new JLabel("Step:"));
		add(stepTextField);
		add(jumpToStepButton);

		update(uiState); // Initial update to set the correct state of buttons
	}

	public void update(SimulationController.UIState uiState) {
		this.uiState = uiState;
		SimulationState state = uiState.state;
		SwingUtilities.invokeLater(() -> {
			playPauseButton.setEnabled(state != SimulationState.STOPPED);
			playPauseButton.setText(state == SimulationState.RUNNING ? "Pause" : "Play");
			stepForwardButton.setEnabled(state == SimulationState.PAUSED || state == SimulationState.IDLE);
			stepBackButton.setEnabled(state == SimulationState.PAUSED || state == SimulationState.STOPPED);
			resetButton.setEnabled(state != SimulationState.IDLE);
			jumpToStepButton.setEnabled(state != SimulationState.RUNNING);
		});
	}
}
