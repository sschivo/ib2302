package gui;

import framework.*;
import facade.SimulationController;
//import week1.MyRandom;
//import week78.BrachaTouegProcess;

import javax.swing.*;
import java.util.function.Supplier;

public class NetworkVisualizerMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			setUnCaughtExceptionHandler();

			// Create the simulator and controller
			Supplier<Network> networkSupplier = NetworkVisualizerMain::createSampleNetwork;
			NetworkSimulator simulator = new NetworkSimulator(networkSupplier);
			SimulationManager simulationManager = new SimulationManager(simulator);
			SimulationController controller = new SimulationController(simulationManager);

			// Create and show GUI
			GraphVisualizer graphVisualizer = new GraphVisualizer();
			SimulationControlPanel controlPanel = new SimulationControlPanel(controller);
			SimulationStatusPanel statusPanel = new SimulationStatusPanel(controller);
			NetworkVisualizerFrame frame = new NetworkVisualizerFrame(controller, graphVisualizer, controlPanel, statusPanel);

			// Set the frame to show only active edges (i.e., edges that have messages)
			frame.setShowOnlyActiveEdges(true);
		});
	}

	/**
	 * Creates a sample network for testing.
	 *
	 * @return the sample network
	 */
	private static Network createSampleNetwork() {
		Network network = new Network(true);
		network.addProcess("A", "framework.TestProcess");
		network.addProcess("B", "framework.TestProcess");
		network.addProcess("C", "framework.TestProcess");
		network.addChannel("A", "B");
		network.addChannel("B", "C");
		network.addChannel("C", "A");
		network.addChannel("A", "C");

		// Add messages to channels
		network.getProcess("A").send(new DefaultMessage("Msg1"), network.getChannel("A", "B"));
		network.getProcess("B").send(new DefaultMessage("Msg2"), network.getChannel("B", "C"));
		network.getProcess("C").send(new DefaultMessage("Msg3"), network.getChannel("C", "A"));
		network.getProcess("A").send(new DefaultMessage("Msg4"), network.getChannel("A", "C"));

		return network;
	}

//	private static Network rockPaperScissorsMultiRounds() {
//		Network n = new Network(true);
//		for (int i = 0; i < 6; i++) {
//			n.addProcess("p" + i, "week1.RockPaperScissorsMultiRoundsProcess");
//		}
//		n.makeComplete();
//		MyRandom.random.setSeed(2);
//		return n;
//	}
//
//	private static void addRequest(Network n, String from, String to) {
//		BrachaTouegProcess p = (BrachaTouegProcess) n.getProcess(from);
//		BrachaTouegProcess q = (BrachaTouegProcess) n.getProcess(to);
//		Channel pq = n.getChannel(from, to);
//
//		p.addOutRequest(pq);
//		q.addInRequest(pq);
//	}
//
//	// Example 5.3 (slides)
//	// Deadlocked
//	private static Network brachaTouegNetwork1() {
//		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
//		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
//		((BrachaTouegProcess) n.getProcess("v")).setRequests(2);
//		((BrachaTouegProcess) n.getProcess("w")).setRequests(2);
//		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);
//
//		addRequest(n, "u", "w");
//		addRequest(n, "u", "x");
//		addRequest(n, "v", "u");
//		addRequest(n, "v", "x");
//		addRequest(n, "w", "v");
//		addRequest(n, "w", "x");
//
//		return n;
//	}
//
//	// Example 5.4 (slides)
//	// Free
//	private static Network brachaTouegNetwork2() {
//		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
//		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
//		((BrachaTouegProcess) n.getProcess("v")).setRequests(2);
//		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
//		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);
//
//		addRequest(n, "u", "w");
//		addRequest(n, "u", "x");
//		addRequest(n, "v", "u");
//		addRequest(n, "v", "x");
//		addRequest(n, "w", "v");
//		addRequest(n, "w", "x");
//
//		return n;
//	}
//
//	// Example 5.5 (slides)
//	// Free
//	private static Network brachaTouegNetwork3() {
//		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
//		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
//		((BrachaTouegProcess) n.getProcess("v")).setRequests(1);
//		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
//		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);
//
//		addRequest(n, "u", "v");
//		addRequest(n, "u", "x");
//		addRequest(n, "v", "w");
//		addRequest(n, "w", "x");
//
//		return n;
//	}
//
//	// Custom network
//	// Free (trivially)
//	private static Network brachaTouegNetwork4() {
//		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
//		((BrachaTouegProcess) n.getProcess("u")).setRequests(0);
//		((BrachaTouegProcess) n.getProcess("v")).setRequests(1);
//		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
//		((BrachaTouegProcess) n.getProcess("x")).setRequests(2);
//
//		addRequest(n, "v", "u");
//		addRequest(n, "w", "u");
//		addRequest(n, "x", "v");
//		addRequest(n, "x", "w");
//
//		return n;
//	}

	private static void setUnCaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			e.printStackTrace();
			System.exit(1);
		});
	}
}
