package framework;

import helper.CustomScheduledExecutorService;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manages the simulation of a network, providing methods to control the simulation state.
 */
public class SimulationManager {
	/**
	 * The network simulator being managed.
	 */
	private final NetworkSimulator simulator;
	/**
	 * The current state of the simulation.
	 */
	private volatile SimulationState state = SimulationState.IDLE;
	/**
	 * The delay between simulation steps in milliseconds.
	 */
	private long delay = 100;
	/**
	 * The last exception that occurred during the simulation.
	 */
	private IllegalReceiveException lastException;
	/**
	 * The executor service that will be used to schedule simulation steps.
	 */
	private final CustomScheduledExecutorService executorService = new CustomScheduledExecutorService(1);

	/**
	 * This is the publisher that will be used to signal changes to consumers.
	 * It is a synchronous SubmissionPublisher that will run the consumers on the calling thread.
	 */
	private final SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>(Runnable::run, 1);

	/**
	 * Constructs a SimulationManager with the given simulator.
	 *
	 * @param simulator the network simulator
	 */
	public SimulationManager(NetworkSimulator simulator) {
		this.simulator = simulator;
	}

	/**
	 * This method allows consumers to subscribe to the publisher.
	 *
	 * @param consumer the consumer that will be called when the publisher signals a change.
	 */
	public void consume(Consumer<Integer> consumer) {
		publisher.consume(consumer);
	}

	/**
	 * Gets the current state of the simulation.
	 *
	 * @return the current simulation state
	 */
	public SimulationState getState() {
		return state;
	}

	/**
	 * Sets the delay between simulation steps.
	 *
	 * @param delay the delay in milliseconds
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * Gets the last exception that occurred during the simulation.
	 *
	 * @return the last exception, or null if no exception has occurred
	 */
	public IllegalReceiveException getLastException() {
		return lastException;
	}

	/**
	 * Peeks at the next message that will be delivered.
	 *
	 * @return the next message, or null if there are no messages
	 */
	public ChannelMessage peekMessage() {
		return simulator.peekMessage();
	}

	/**
	 * Gets the network being simulated.
	 *
	 * @return the network
	 */
	public Network getNetwork() {
		return simulator.getNetwork();
	}

	/**
	 * Gets the current step of the simulation.
	 *
	 * @return the current step
	 */
	public int getCurrentStep() {
		return simulator.getCurrentStep();
	}

	/**
	 * Initializes the simulation.
	 */
	public void initSimulation() {
		runAsync(() -> {
			init();
			notifyUpdate();
		});
	}

	/**
	 * Resumes the simulation.
	 */
	public void resumeSimulation() {
		runAsync(() -> {
			if (state != SimulationState.PAUSED && state != SimulationState.IDLE) {
				throw new IllegalStateException("Simulation must be paused or idle to resume");
			}
			if (state == SimulationState.IDLE) {
				init();
				notifyUpdate();
				if (state == SimulationState.STOPPED) {
					return;
				}
			}
			state = SimulationState.RUNNING;
			scheduleNextStep();
		});
	}

	/**
	 * Pauses the simulation.
	 */
	public void pauseSimulation() {
		runAsync(() -> {
			if (state != SimulationState.RUNNING) {
				throw new IllegalStateException("Simulation must be running to pause");
			}
			state = SimulationState.PAUSED;
			notifyUpdate();
		});
	}

	/**
	 * Steps the simulation forward by one step.
	 */
	public void stepSimulation() {
		runAsync(() -> {
			if (state != SimulationState.PAUSED && state != SimulationState.IDLE) {
				throw new IllegalStateException("Simulation must be paused or idle to step");
			}
			if (state == SimulationState.IDLE) {
				init();
			} else {
				step();
			}
			notifyUpdate();
		});
	}

	/**
	 * Steps the simulation backward by one step.
	 */
	public void stepBackSimulation() {
		runAsync(() -> {
			if (state != SimulationState.PAUSED && state != SimulationState.STOPPED) {
				throw new IllegalStateException("Simulation must be paused or stopped to step back");
			}
			try {
				int targetStep = simulator.getCurrentStep() - 1;
				reset();
				if (!simulator.jumpToStep(targetStep)) {
					state = SimulationState.STOPPED;
				} else {
					if (simulator.getCurrentStep() == 0) {
						state = SimulationState.IDLE;
					} else {
						state = SimulationState.PAUSED;
					}
				}
			} catch (IllegalReceiveException e) {
				lastException = e;
				state = SimulationState.STOPPED;
			}
			notifyUpdate();
		});
	}

	/**
	 * Stops the simulation.
	 */
	public void stopSimulation() {
		runAsync(() -> {
			if (state == SimulationState.STOPPED) {
				throw new IllegalStateException("Simulation is already stopped");
			}
			state = SimulationState.STOPPED;
			notifyUpdate();
		});
	}

	/**
	 * Resets the simulation.
	 */
	public void resetSimulation() {
		runAsync(() -> {
			if (state == SimulationState.IDLE) {
				throw new IllegalStateException("Simulation is already reset");
			}
			reset();
			notifyUpdate();
		});
	}

	/**
	 * Jumps to a specific step in the simulation.
	 *
	 * @param step the step to jump to
	 */
	public void jumpToStep(int step) {
		runAsync(() -> {
			if (state == SimulationState.RUNNING) {
				throw new IllegalStateException("Simulation cannot jump to step while running");
			}
			try {
				if (step < simulator.getCurrentStep()) {
					reset();
				}
				if (!simulator.jumpToStep(step)) {
					state = SimulationState.STOPPED;
				} else {
					if (simulator.getCurrentStep() == 0) {
						state = SimulationState.IDLE;
					} else {
						state = SimulationState.PAUSED;
					}
				}
			} catch (IllegalReceiveException e) {
				lastException = e;
				state = SimulationState.STOPPED;
			}
			notifyUpdate();
		});
	}

	/**
	 * Initializes the simulation processes.
	 */
	private void init() {
		if (state != SimulationState.IDLE) {
			throw new IllegalStateException("Simulation must be idle to initialize");
		}
		simulator.init();
		if (peekMessage() != null) {
			state = SimulationState.PAUSED;
		} else {
			state = SimulationState.STOPPED;
		}
	}

	/**
	 * Steps the simulation forward by one step.
	 */
	private void step() {
		try {
			if (!simulator.simulateStep()) {
				state = SimulationState.STOPPED;
			}
		} catch (IllegalReceiveException e) {
			lastException = e;
			state = SimulationState.STOPPED;
		}
	}

	/**
	 * Resets the simulation processes.
	 */
	private void reset() {
		lastException = null;
		simulator.reset();
		state = SimulationState.IDLE;
	}

	/**
	 * Schedules the next step of the simulation after a delay.
	 */
	private void scheduleNextStep() {
		executorService.safeSchedule(() -> {
			if (state == SimulationState.RUNNING) {
				step();
				notifyUpdate();
				scheduleNextStep();
			}
		}, delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * Notifies subscribers of an update.
	 */
	private void notifyUpdate() {
		publisher.submit(0);
	}

	/**
	 * Executes the given task asynchronously.
	 *
	 * @param runnable the task to execute
	 */
	private void runAsync(Runnable runnable) {
		executorService.safeExecute(runnable);
	}
}
