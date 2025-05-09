package gui;

import facade.SimulationController;
import framework.IllegalReceiveException;

import javax.swing.*;
import java.awt.*;

public class SimulationStatusPanel extends JPanel {
	private final JLabel stepLabel;
	private final JLabel exceptionLabel;
	private SimulationController.UIState uiState;

	public SimulationStatusPanel(SimulationController controller) {
		this.stepLabel = new JLabel("Current Step: 0");
		this.exceptionLabel = new JLabel("Exception: None");
		exceptionLabel.setVisible(false);

		setLayout(new GridLayout(2, 1));

		add(stepLabel);
		add(exceptionLabel);

		// initialize the UI state
		uiState = controller.getUIState();

		// subscribe to the controller
		controller.consume(this::update);
	}

	public void update(SimulationController.UIState uiState) {
		this.uiState = uiState;
		SwingUtilities.invokeLater(() -> {
			stepLabel.setText("Current Step: " + uiState.currentStep);
			IllegalReceiveException exception = uiState.lastException;
			if (exception != null) {
				exceptionLabel.setText("IllegalReceiveException occurred!");
				exceptionLabel.setForeground(Color.RED);
				exceptionLabel.setVisible(true);
			} else {
				exceptionLabel.setText("Exception: None");
				exceptionLabel.setVisible(false);
			}
		});
	}
}
