package week2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;

import framework.Network;

class CausalOrderTest {

	@Test
	void constructorTest1() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,q,0)");
		substitutions.put("$b", "s(p,r,1)");
		substitutions.put("$c", "r(p,q,0)");
		substitutions.put("$d", "r(r,q,1)");
		substitutions.put("$e", "r(p,r,1)");
		substitutions.put("$f", "s(r,q,1)");

		Network n = new Network(true);

		CausalOrder expected = CausalOrder.parse(sub("$a<$b $e<$f $d<$c $a<$c $b<$e $f<$d", substitutions), n);

		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $e $f $d $c", substitutions), n)));

		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $e $f $c $d", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $e $c $f $d", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $c $e $f $d", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $c $b $e $f $d", substitutions), n)));
	}

	@Test
	void constructorTest2() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,q,0)");
		substitutions.put("$b", "s(p,r,1)");
		substitutions.put("$c", "r(p,q,0)");
		substitutions.put("$d", "r(r,q,1)");
		substitutions.put("$e", "r(p,r,1)");
		substitutions.put("$f", "s(r,q,1)");

		Network n = new Network(true);

		CausalOrder expected = CausalOrder.parse(sub("$a<$b $e<$f $c<$d $a<$c $b<$e $f<$d", substitutions), n);

		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $e $f $c $d", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $e $c $f $d", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $c $e $f $d", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $c $b $e $f $d", substitutions), n)));

		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $e $f $d $c", substitutions), n)));
	}

	@Test
	void constructorTest3() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "r(q,p,1)");
		substitutions.put("$b", "s(p,q,1)");
		substitutions.put("$c", "s(q,r,0)");
		substitutions.put("$d", "s(q,p,1)");
		substitutions.put("$e", "r(p,q,1)");
		substitutions.put("$f", "r(q,r,0)");

		Network n = new Network(true);

		CausalOrder expected = CausalOrder.parse(sub("$a<$b $c<$d $d<$e $c<$f $d<$a $b<$e", substitutions), n);

		assertEquals(expected, new CausalOrder(Event.parseList(sub("$c $d $a $b $e $f", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$c $d $a $b $f $e", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$c $d $a $f $b $e", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$c $d $f $a $b $e", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$c $f $d $a $b $e", substitutions), n)));
	}

	@Test
	void constructorTest4() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,r,0)");
		substitutions.put("$b", "s(q,r,1)");
		substitutions.put("$c", "r(p,r,0)");
		substitutions.put("$d", "r(q,r,1)");

		Network n = new Network(true);

		CausalOrder expected = CausalOrder.parse(sub("$a<$c $b<$d $c<$d", substitutions), n);

		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $c $b $d", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $c $d", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$b $a $c $d", substitutions), n)));

		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$b $d $a $c", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$b $a $d $c", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $d $c", substitutions), n)));
	}

	@Test
	void constructorTest5() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,r,0)");
		substitutions.put("$b", "s(q,r,1)");
		substitutions.put("$c", "r(p,r,0)");
		substitutions.put("$d", "r(q,r,1)");

		Network n = new Network(true);

		CausalOrder expected = CausalOrder.parse(sub("$a<$c $b<$d $d<$c", substitutions), n);

		assertEquals(expected, new CausalOrder(Event.parseList(sub("$b $d $a $c", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$b $a $d $c", substitutions), n)));
		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $d $c", substitutions), n)));

		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $c $b $d", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $c $d", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$b $a $c $d", substitutions), n)));
	}

	@Test
	void constructorTest6() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "a@p");
		substitutions.put("$b", "b@p");
		substitutions.put("$c", "c@p");

		Network n = new Network(true);

		CausalOrder expected = CausalOrder.parse(sub("$a<$b $b<$c", substitutions), n);

		assertEquals(expected, new CausalOrder(Event.parseList(sub("$a $b $c", substitutions), n)));

		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$a $c $b", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$b $a $c", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$b $c $a", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$c $a $b", substitutions), n)));
		assertNotEquals(expected, new CausalOrder(Event.parseList(sub("$c $b $a", substitutions), n)));
	}
	
	@Test
	void toComputationTest1() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,q,0)");
		substitutions.put("$b", "s(p,r,1)");
		substitutions.put("$c", "r(p,q,0)");
		substitutions.put("$d", "r(r,q,1)");
		substitutions.put("$e", "r(p,r,1)");
		substitutions.put("$f", "s(r,q,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d $e $f", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$a $b $e $f $d $c", substitutions), n));

		Set<List<Event>> actual = CausalOrder.parse(sub("$a<$b $e<$f $d<$c $a<$c $b<$e $f<$d", substitutions), n)
				.toComputation(events);

		assertEquals(expected, actual);
	}

	@Test
	void toComputationTest2() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,q,0)");
		substitutions.put("$b", "s(p,r,1)");
		substitutions.put("$c", "r(p,q,0)");
		substitutions.put("$d", "r(r,q,1)");
		substitutions.put("$e", "r(p,r,1)");
		substitutions.put("$f", "s(r,q,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d $e $f", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$a $b $e $f $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $e $c $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $c $e $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $c $b $e $f $d", substitutions), n));

		Set<List<Event>> actual = CausalOrder.parse(sub("$a<$b $e<$f $c<$d $a<$c $b<$e $f<$d", substitutions), n)
				.toComputation(events);

		assertEquals(expected, actual);
	}

	@Test
	void toComputationTest3() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,q,0)");
		substitutions.put("$b", "s(p,r,1)");
		substitutions.put("$c", "r(p,q,0)");
		substitutions.put("$d", "r(r,q,1)");
		substitutions.put("$e", "r(p,r,1)");
		substitutions.put("$f", "s(r,q,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d $e $f", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$a $b $e $f $d $c", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $e $f $d $c", substitutions), n));
		expected.add(Event.parseList(sub("$b $e $a $f $d $c", substitutions), n));
		expected.add(Event.parseList(sub("$b $e $f $a $d $c", substitutions), n));
		expected.add(Event.parseList(sub("$b $e $f $d $a $c", substitutions), n));

		assertEquals(expected,
				CausalOrder.parse(sub("$e<$f $d<$c $a<$c $b<$e $f<$d", substitutions), n).toComputation(events));
	}

	@Test
	void toComputationTest4() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,q,0)");
		substitutions.put("$b", "s(p,r,1)");
		substitutions.put("$c", "r(p,q,0)");
		substitutions.put("$d", "r(r,q,1)");
		substitutions.put("$e", "r(p,r,1)");
		substitutions.put("$f", "s(r,q,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d $e $f", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$a $b $e $f $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $e $f $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $e $a $f $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $e $f $a $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $e $c $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $e $c $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $e $a $c $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $c $e $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $c $e $f $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $c $b $e $f $d", substitutions), n));

		assertEquals(expected,
				CausalOrder.parse(sub("$e<$f $c<$d $a<$c $b<$e $f<$d", substitutions), n).toComputation(events));
	}

	@Test
	void toComputationTest5() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "r(q,p,1)");
		substitutions.put("$b", "s(p,q,1)");
		substitutions.put("$c", "s(q,r,0)");
		substitutions.put("$d", "s(q,p,1)");
		substitutions.put("$e", "r(p,q,1)");
		substitutions.put("$f", "r(q,r,0)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d $e $f", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$c $d $a $b $e $f", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $a $b $f $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $a $f $b $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $f $a $b $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $f $d $a $b $e", substitutions), n));

		assertEquals(expected,
				CausalOrder.parse(sub("$a<$b $c<$d $d<$e $c<$f $d<$a $b<$e", substitutions), n).toComputation(events));
	}

	@Test
	void toComputationTest6() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "r(q,p,1)");
		substitutions.put("$b", "s(p,q,1)");
		substitutions.put("$c", "s(q,r,0)");
		substitutions.put("$d", "s(q,p,1)");
		substitutions.put("$e", "r(p,q,1)");
		substitutions.put("$f", "r(q,r,0)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d $e $f", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$c $d $a $b $e $f", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $b $a $e $f", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $a $b $f $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $b $f $a $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $b $a $f $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $f $b $a $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $f $d $b $a $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $a $f $b $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $d $f $a $b $e", substitutions), n));
		expected.add(Event.parseList(sub("$c $f $d $a $b $e", substitutions), n));

		assertEquals(expected, CausalOrder.parse(sub("$c<$d $d<$e $c<$f $d<$a $b<$e $d<$b $a<$e", substitutions), n)
				.toComputation(events));
	}

	@Test
	void toComputationTest7() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,r,0)");
		substitutions.put("$b", "s(q,r,1)");
		substitutions.put("$c", "r(p,r,0)");
		substitutions.put("$d", "r(q,r,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$a $c $b $d", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $c $d", substitutions), n));

		assertEquals(expected, CausalOrder.parse(sub("$a<$c $b<$d $c<$d", substitutions), n).toComputation(events));
	}

	@Test
	void toComputationTest8() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,r,0)");
		substitutions.put("$b", "s(q,r,1)");
		substitutions.put("$c", "r(p,r,0)");
		substitutions.put("$d", "r(q,r,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$b $d $a $c", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $d $c", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $d $c", substitutions), n));

		assertEquals(expected, CausalOrder.parse(sub("$a<$c $b<$d $d<$c", substitutions), n).toComputation(events));
	}

	@Test
	void toComputationTest9() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,r,0)");
		substitutions.put("$b", "s(q,r,1)");
		substitutions.put("$c", "r(p,r,0)");
		substitutions.put("$d", "r(q,r,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$a $c $b $d", substitutions), n));
		expected.add(Event.parseList(sub("$c $a $b $d", substitutions), n));
		expected.add(Event.parseList(sub("$c $b $a $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $c $a $d", substitutions), n));
		expected.add(Event.parseList(sub("$c $b $d $a", substitutions), n));
		expected.add(Event.parseList(sub("$b $c $d $a", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $c $d", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $c $d", substitutions), n));

		assertEquals(expected, CausalOrder.parse(sub("$b<$d $c<$d", substitutions), n).toComputation(events));
	}

	@Test
	void toComputationTest10() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "s(p,r,0)");
		substitutions.put("$b", "s(q,r,1)");
		substitutions.put("$c", "r(p,r,0)");
		substitutions.put("$d", "r(q,r,1)");

		Network n = new Network(true);
		Set<Event> events = new LinkedHashSet<>(Event.parseList(sub("$a $b $c $d", substitutions), n));

		Set<List<Event>> expected = new HashSet<>();
		expected.add(Event.parseList(sub("$b $d $a $c", substitutions), n));
		expected.add(Event.parseList(sub("$b $d $c $a", substitutions), n));
		expected.add(Event.parseList(sub("$b $a $d $c", substitutions), n));
		expected.add(Event.parseList(sub("$a $b $d $c", substitutions), n));

		assertEquals(expected, CausalOrder.parse(sub("$b<$d $d<$c", substitutions), n).toComputation(events));
	}

	private String sub(String s, Map<String, String> substitutions) {
		for (Entry<String, String> substitution : substitutions.entrySet()) {
			s = s.replace(substitution.getKey(), substitution.getValue());
		}
		return s;
	}
}
