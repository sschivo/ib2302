package framework;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simulator for a network of processes.
 */
public class NetworkSimulator {
	private final Supplier<Network> networkSupplier;
	private Network network;
	private int currentStep;
	private boolean initialized;

	/**
	 * Constructs a NetworkSimulator with the given network supplier.
	 *
	 * @param networkSupplier a supplier that provides a new network
	 */
	public NetworkSimulator(Supplier<Network> networkSupplier) {
		this.networkSupplier = networkSupplier;
		reset();
	}

	/**
	 * Gets the current step of the simulation.
	 *
	 * @return the current step
	 */
	public int getCurrentStep() {
		return currentStep;
	}

	/**
	 * Gets the network being simulated.
	 *
	 * @return the network
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Resets the simulation to its initial state.
	 */
	public void reset() {
		currentStep = 0;
		network = networkSupplier.get();
		initialized = false;
	}

	/**
	 * Initializes the simulation.
	 * @throws IllegalStateException if the simulation is already initialized or has started
	 */
	public void init() {
		if (initialized) {
			throw new IllegalStateException("Cannot initialize twice");
		}
		if (currentStep != 0) {
			throw new IllegalStateException("Cannot initialize after simulation has started");
		}
		initializeProcesses();
		currentStep = 1;
		initialized = true;
	}

	/**
	 * Peek at the next message that will be delivered.
	 *
	 * @return the next message that will be delivered, or null if there are no messages.
	 */
	public ChannelMessage peekMessage() {
		return pickMessage();
	}

	/**
	 * Simulate one step of the network.
	 *
	 * @return true if a message was delivered, false if there are no more messages.
	 * @throws IllegalReceiveException if a process receives a message it should not.
	 * @throws IllegalStateException if the simulation has not been initialized.
	 */
	public boolean simulateStep() throws IllegalReceiveException {
		if (!initialized) {
			throw new IllegalStateException("Cannot simulate before initializing");
		}
		ChannelMessage nextMessage = pickMessage();
		if (nextMessage == null) {
			return false;
		}
		nextMessage.channel.take();
		nextMessage.channel.getReceiver().receive(nextMessage.message, nextMessage.channel);
		currentStep++;
		return true;
	}

	/**
	 * Jump to a specific step in the simulation.
	 *
	 * @param targetStep the step to jump to (must be greater than the current step)
	 * @return true if the jump was successful, false if the simulation ended before reaching the target step
	 * @throws IllegalReceiveException if a process receives a message it should not
	 */
	public boolean jumpToStep(int targetStep) throws IllegalReceiveException {
		if (currentStep == 0 && targetStep > 0) {
			init();
		}
		while (currentStep < targetStep) {
			if (!simulateStep()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Initializes all processes in the network.
	 */
	private void initializeProcesses() {
		for (Process p : network.getProcesses().values()) {
			p.init();
		}
	}

	/**
	 * Picks the next message to be delivered.
	 *
	 * @return the next message, or null if there are no messages
	 */
	private ChannelMessage pickMessage() {
		Collection<Channel> channels = new HashSet<>();
		for (Map<String, Channel> foo : network.getChannels().values()) {
			channels.addAll(foo.values());
		}
		for (Channel c : channels) {
			if (!c.getContent().isEmpty()) {
				return new ChannelMessage(c.getContent().iterator().next(), c);
			}
		}
		return null;
	}
}
