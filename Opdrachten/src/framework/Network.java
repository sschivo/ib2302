package framework;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

public class Network {

	private Map<String, Process> processes = new LinkedHashMap<>();
	private Map<String, Map<String, Channel>> channels = new LinkedHashMap<>();
	private boolean channelsAreFifo;

	public Network(boolean channelsAreFifo) {
		this.channelsAreFifo = channelsAreFifo;
	}

	@Override
	public String toString() {
		String s = "";
		for (Process p : processes.values()) {
			s += p.toString() + " ";
		}
		for (Map<String, Channel> channelsSender : channels.values()) {
			for (Channel c : channelsSender.values()) {
				s += c.toString() + " ";
			}
		}
		return s.trim();
	}

	public Network addProcess(String name, String type) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Process> c = (Class<? extends Process>) Class.forName(type);
			Process p = c.newInstance();
			p.setName(name);
			processes.putIfAbsent(name, p);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

		return this;
	}

	public Network addProcess(Process p) {
		processes.putIfAbsent(p.getName(), p);
		return this;
	}

	public Network addChannel(String nameSender, String nameReceiver) {
		Process sender = processes.get(nameSender);
		Process receiver = processes.get(nameReceiver);

		if (!channels.containsKey(nameSender)) {
			channels.put(nameSender, new LinkedHashMap<>());
		}

		Map<String, Channel> channelsSender = channels.get(nameSender);
		if (!channelsSender.containsKey(nameReceiver)) {
			Channel c = channelsAreFifo ? new ListChannel(sender, receiver) : new SetChannel(sender, receiver);
			channelsSender.put(nameReceiver, c);
		}

		return this;
	}

	public Network addRing() {
		Process first = null;
		Process last = null;
		for (Process p : processes.values()) {
			if (first == null) {
				first = p;
				last = p;
				continue;
			}

			addChannel(last.getName(), p.getName());
			last = p;
		}
		addChannel(last.getName(), first.getName());

		return this;
	}

	public boolean containsChannel(String nameSender, String nameReceiver) {
		return channels.containsKey(nameSender) && channels.get(nameSender).containsKey(nameReceiver);
	}

	public boolean containsProcess(String name) {
		return processes.containsKey(name);
	}

	public Channel getChannel(String nameSender, String nameReceiver) {
		if (!containsChannel(nameSender, nameReceiver)) {
			throw new IllegalStateException();
		}
		return channels.get(nameSender).get(nameReceiver);
	}

	public Map<String, Map<String, Channel>> getChannels() {
		return new LinkedHashMap<>(channels);
	}

	public Process getProcess(String name) {
		if (!containsProcess(name)) {
			throw new IllegalStateException();
		}
		return processes.get(name);
	}

	public Map<String, Process> getProcesses() {
		return new LinkedHashMap<>(processes);
	}

	public Network makeComplete() {
		for (Process p : processes.values()) {
			for (Process q : processes.values()) {
				if (!p.equals(q)) {
					addChannel(p.getName(), q.getName());
				}
			}
		}

		return this;
	}

	public Network makeUndirected() {
		for (Map<String, Channel> channelsSender : channels.values()) {
			for (Channel c : channelsSender.values()) {
				addChannel(c.getReceiver().getName(), c.getSender().getName());
			}
		}
		return this;
	}

	public static Network parse(boolean channelsAreFifo, String s) {
		Network n = new Network(channelsAreFifo);

		String[] tokens = s.split(" ");
		for (String token : tokens) {
			String[] subtokens;

			try {
				if ((subtokens = token.split(":")).length == 2) {
					String subsubtokens = subtokens[0];
					String type = subtokens[1];
					for (String name : subsubtokens.split(",")) {
						n.addProcess(name, type);
					}

				} else if ((subtokens = token.split("->")).length == 2) {
					String nameSender = subtokens[0];
					String nameReceiver = subtokens[1];
					n.addChannel(nameSender, nameReceiver);

				} else {
					throw new Exception();
				}

			} catch (Throwable t) {
				throw new IllegalArgumentException(token + " in " + s);
			}
		}

		return n;
	}

	public boolean simulate(Map<String, Collection<String>> output) throws IllegalReceiveException {
		int max_iterations = 10000;
		int iterations = 0;
		Message m;

		// Get a collection of all processes
		Collection<Process> processes = getProcesses().values();

		// Get a collection of all channels
		Collection<Channel> channels = new HashSet<Channel>();
		for (Map<String, Channel> foo : getChannels().values()) {
			for (Channel bar : foo.values()) {
				channels.add(bar);
			}
		}

		// Initialise all processes
		for (Process p : processes) {
			p.init();
		}

		// Let them run
		while (iterations < max_iterations) {
			iterations++;

			System.out.println("");
			System.out.println("===== Iteration " + iterations + " =====");
			// Sanity check
			for (Channel c : channels) {
				System.out.println(c.toString() + ": " + c.getContent().toString());
			}
			// System.out.println("");

			// Pick a message
			m = Message.DUMMY;
			for (Channel c : channels) {
				if (!c.getContent().isEmpty()) {
					m = c.take();
					System.out.println("Message #" + iterations + ": " + m.toString() + " (" + c.toString() + ")");
					System.out.println("");

					try {
						c.getReceiver().receive(m, c);
					} catch (IllegalReceiveException e) {
						throw e;
					}

					break;
				}
			}

			if (m == Message.DUMMY) {
				iterations--;
				break;
			}
		}

		// Reached iteration bound
		if (iterations >= max_iterations) {
			System.out.println("Reached the cap of " + max_iterations + " iterations.");
			return false;
		}

		for (Process p : processes) {
			output.put(p.getName(), ProcessTests.getPrinted(p));
		}

		// Return results
		return true;
	}
}
