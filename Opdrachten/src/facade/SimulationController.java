package facade;

import framework.ChannelMessage;
import framework.IllegalReceiveException;
import framework.SimulationManager;
import framework.SimulationState;
import graph.Graph;
import graph.NetworkToGraphConverter;

import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class SimulationController {
	private final SimulationManager simulationManager;
	boolean showOnlyActiveEdges;
	private final SubmissionPublisher<UIState> publisher = new SubmissionPublisher<>(Runnable::run, 1);
	private UIState uiState;

	public SimulationController(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;
		simulationManager.consume((v) -> publisher.submit(createUIState()));
		uiState = createUIState();
	}

	/**
	 * This method allows consumers to subscribe to the publisher.
	 * @param consumer the consumer that will be called when the publisher signals a change.
	 */
	public void consume(Consumer<UIState> consumer) {
		publisher.consume(consumer);
	}

	public void resumeSimulation() {
		simulationManager.resumeSimulation();
	}

	public void pauseSimulation() {
		simulationManager.pauseSimulation();
	}

	public void stepSimulation() {
		simulationManager.stepSimulation();
	}

	public void stepBackSimulation() {
		simulationManager.stepBackSimulation();
	}

	public void stopSimulation() {
		simulationManager.stopSimulation();
	}

	public void resetSimulation() {
		simulationManager.resetSimulation();
	}

	public void jumpToStep(int step) {
		simulationManager.jumpToStep(step);
	}

	public UIState getUIState() {
		return uiState;
	}

	public void setDelay(long delay) {
		simulationManager.setDelay(delay);
	}

	public void setShowOnlyActiveEdges(boolean showOnlyActiveEdges) {
		this.showOnlyActiveEdges = showOnlyActiveEdges;
		publisher.submit(createUIState());
	}

	private UIState createUIState() {
		uiState = new UIState(
				simulationManager.getState(),
				simulationManager.getCurrentStep(),
				getGraph(),
				simulationManager.getLastException()
		);
		return uiState;
	}

	private Graph getGraph() {
		ChannelMessage nextMessage = simulationManager.peekMessage();
		if (nextMessage == null) {
			return NetworkToGraphConverter.convert(
					simulationManager.getNetwork(),
					showOnlyActiveEdges
			);
		} else {
			return NetworkToGraphConverter.convert(
					simulationManager.getNetwork(),
					showOnlyActiveEdges,
					nextMessage.channel,
					nextMessage.message
			);
		}
	}

	public static class UIState {
		public final SimulationState state;
		public final int currentStep;
		public final Graph graph;
		public final IllegalReceiveException lastException;

		public UIState(SimulationState state, int currentStep, Graph graph, IllegalReceiveException lastException) {
			this.state = state;
			this.currentStep = currentStep;
			this.graph = graph;
			this.lastException = lastException;
		}
	}
}
