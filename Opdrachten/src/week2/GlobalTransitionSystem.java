package week2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;

import framework.DefaultProcess;
import framework.Network;
import framework.Process;

public class GlobalTransitionSystem {

	private Map<Configuration, Map<Event, Configuration>> transitions = new LinkedHashMap<>();
	private Configuration initial;
	
	public boolean hasExecution(List<Configuration> sequence) {
		// TODO
		return false;
	}
	
	/*
	 * -------------------------------------------------------------------------
	 */
	
	@Override
	public String toString() {
		return toString(new LinkedHashMap<>());
	}

	public String toString(Map<String, Configuration> configurations) {

		Map<Configuration, String> ids = new LinkedHashMap<Configuration, String>();
		for (String id : configurations.keySet()) {
			Configuration c = configurations.get(id);
			if (ids.containsKey(c)) {
				throw new IllegalArgumentException();
			}

			ids.put(c, id);
		}

		Supplier<String> freshId = new Supplier<String>() {
			@Override
			public String get() {
				int n = ids.size();
				while (ids.values().contains("g" + n)) {
					n++;
				}
				return "g" + n;
			}
		};

		StringBuilder b = new StringBuilder();

		if (!ids.containsKey(initial)) {
			ids.put(initial, freshId.get());
		}

		b.append(ids.get(initial));

		for (Configuration source : transitions.keySet()) {
			if (!ids.containsKey(source)) {
				ids.put(source, freshId.get());
			}

			Map<Event, Configuration> sourceTransitions = transitions.get(source);
			for (Event label : sourceTransitions.keySet()) {
				Configuration target = sourceTransitions.get(label);
				if (!ids.containsKey(target)) {
					ids.put(target, freshId.get());
				}

				b.append(" ").append(ids.get(source)).append("--").append(label).append("->").append(ids.get(target));
			}
		}

		return b.toString();
	}

	public void addTransition(Configuration source, Event label, Configuration target) {
		if (!transitions.containsKey(source)) {
			transitions.put(source, new LinkedHashMap<>());
		}

		Map<Event, Configuration> fromTransitions = transitions.get(source);
		if (fromTransitions.containsKey(label)) {
			throw new IllegalStateException();
		}

		fromTransitions.put(label, target);
	}

	public void setInitial(Configuration c) {
		if (initial != null) {
			throw new IllegalStateException();
		}

		initial = c;
	}

	public List<Configuration> randomExecution(long seed) {

		Set<Configuration> terminals = new HashSet<>();
		for (Configuration source : transitions.keySet()) {
			for (Configuration target : transitions.get(source).values()) {
				if (!transitions.containsKey(target)) {
					terminals.add(target);
				}
			}
		}

		if (terminals.isEmpty()) {
			throw new IllegalStateException();
		}

		for (Configuration c : transitions.keySet()) {
			if (!terminals.contains(c) && Collections.disjoint(terminals, transitions.get(c).values())) {
				throw new IllegalArgumentException();
			}
		}

		Random r = new Random(seed);

		Stack<Configuration> sequence = new Stack<>();

		sequence.push(initial);
		while (true) {
			if (!transitions.containsKey(sequence.peek())) {
				break;
			}

			Collection<Configuration> targets = transitions.get(sequence.peek()).values();
			int i = 0, n = r.nextInt(targets.size());
			for (Configuration target : targets) {
				if (i++ == n) {
					sequence.push(target);
					break;
				}
			}
		}

		return sequence;
	}

	public List<Configuration> randomNonExecution(long seed) {
		List<Configuration> sequence = randomExecution(seed);

		Random r = new Random(seed);
		r.nextInt();
		switch (r.nextInt(4)) {
		case 0:
			sequence.remove(0);
			break;
		case 1:
			sequence.remove(sequence.size() - 1);
			break;
		case 2:
			if (sequence.size() > 2) {
				int i;
				do {
					i = 1 + r.nextInt(sequence.size() - 2);
					sequence.remove(i);
				} while (sequence.size() > 2 && transitions.get(sequence.get(i - 1)).containsValue(sequence.get(i)));
			}
			if (sequence.size() == 2) {
				sequence.clear();
			}
			break;
		case 3:
			sequence.add(r.nextInt(sequence.size()), new Configuration());
			break;
		}

		return sequence;
	}

	public static GlobalTransitionSystem parse(String s, Map<String, Configuration> configurations) {

		GlobalTransitionSystem system = new GlobalTransitionSystem();

		Network n = new Network(true);

		String[] tokens = s.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if (i == 0) {
				system.setInitial(Configuration.parse(tokens[0], configurations));
			} else {

				String[] subtokens = tokens[i].split("--");
				if (subtokens.length != 2) {
					throw new IllegalArgumentException(tokens[i]);
				}

				String[] subsubtokens = subtokens[1].split("->");
				if (subsubtokens.length != 2) {
					throw new IllegalArgumentException();
				}

				String from = subtokens[0];
				String to = subsubtokens[1];
				String occurs = subsubtokens[0];

				system.addTransition(Configuration.parse(from, configurations), Event.parse(occurs, n),
						Configuration.parse(to, configurations));
			}
		}

		return system;
	}

	public static GlobalTransitionSystem random(long seed, int nProcesses, int nConfigurations, int nTransitions) {
		if (2 * nTransitions < nConfigurations) {
			throw new IllegalArgumentException();
		}

		Random r = new Random(seed);

		Process[] processes = new Process[nProcesses];
		for (int i = 0; i < processes.length; i++) {
			processes[i] = new DefaultProcess("p" + i);
		}

		Configuration[] configurations = new Configuration[nConfigurations];
		for (int i = 0; i < configurations.length; i++) {
			configurations[i] = new Configuration();
		}

		GlobalTransitionSystem system = new GlobalTransitionSystem();
		system.setInitial(configurations[0]);

		List<Configuration> reachable = new ArrayList<>();
		reachable.add(configurations[0]);

		for (int i = 1; i < nConfigurations; i++) {
			Configuration source = reachable.get(r.nextInt(reachable.size()));
			Configuration target = configurations[i];
			Event label = new InternalEvent(processes[r.nextInt(processes.length)], "a" + i);
			system.addTransition(source, label, target);

			if (!reachable.contains(target)) {
				reachable.add(target);
			}
		}

		for (int i = 0; i < nTransitions - 2 * (nConfigurations - 1); i++) {
			int sourceIndex, targetIndex;
			do {
				sourceIndex = r.nextInt(configurations.length - 1);
				targetIndex = r.nextInt(configurations.length);
			} while (sourceIndex == targetIndex);

			Configuration source = configurations[sourceIndex];
			Configuration target = configurations[targetIndex];
			Event label = new InternalEvent(processes[r.nextInt(processes.length)], "b" + i);
			system.addTransition(source, label, target);
		}

		for (int i = 0; i < nConfigurations - 1; i++) {
			Configuration source = configurations[i];
			Configuration target = configurations[nConfigurations - 1];
			Event label = new InternalEvent(processes[r.nextInt(processes.length)], "c" + i);
			system.addTransition(source, label, target);
		}

		return system;
	}
}
