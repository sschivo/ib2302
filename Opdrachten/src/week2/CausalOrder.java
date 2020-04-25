package week2;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import framework.Network;

public class CausalOrder {

	private Set<Pair> pairs = new LinkedHashSet<>();

	public CausalOrder() {
	}

	public CausalOrder(List<Event> sequence) {
		// TODO
	}

	public Set<List<Event>> toComputation(Set<Event> events) {
		// TODO
		return null;
	}
	
	/*
	 * -------------------------------------------------------------------------
	 */

	@Override
	public boolean equals(Object o) {
		if (o instanceof CausalOrder) {
			CausalOrder that = (CausalOrder) o;
			return this.pairs.equals(that.pairs);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pairs.size();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Pair p : pairs) {
			b.append(" ").append(p);
		}
		return b.toString().trim();
	}

	public void addPair(Event left, Event right) {
		pairs.add(new Pair(left, right));
	}

	public Set<Pair> getPairs() {
		return new LinkedHashSet<>(pairs);
	}

	public static CausalOrder parse(String s, Network n) {

		CausalOrder order = new CausalOrder();

		Map<String, Event> events = new LinkedHashMap<>();

		String[] tokens = s.split(" ");
		for (String token : tokens) {

			String[] subtokens = token.split("<");
			if (subtokens.length != 2) {
				throw new IllegalArgumentException();
			}

			String left = subtokens[0];
			String right = subtokens[1];

			if (!events.containsKey(left)) {
				events.put(left, Event.parse(left, n));
			}
			if (!events.containsKey(right)) {
				events.put(right, Event.parse(right, n));
			}

			order.addPair(events.get(left), events.get(right));
		}

		return order;
	}
}
