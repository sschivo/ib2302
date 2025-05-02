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

/**
 * Unit tests for CausalOrder.parse(...).toComputation(...)
 */

class CausalOrderTest {

	/**
	 * constructorTest1:
	 * See exercise I-1 from the exercises on Chapter 2
	 * Three processes (p, q, r)
	 * p:
	 *  init {
	 *    send<0> thru pq (a)
	 *    send<1> thru pr (b)
	 *  }
	 * q:
	 *  recv<0> thru pq (c) {}
	 *  recv<1> thru rq (d) {}
	 * r:
	 *  recv<1> thru pr (e) {
	 *    send<1> thru rq (f)
	 *  }
	 *
	 * We consider the computation where d < c,
	 * then provide possible executions in this computation
	 * and check that they all lead to the correct causal
	 * ordering of the events.
	 * We do *not* want any execution where c comes before d
	 * to also lead to this set of rules: hence all the assertNotEquals
	 * (see also the solution of exercise I-1b)
	 */
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

	/**
	 * constructorTest2:
	 * See exercise I-1 from the exercises on Chapter 2
	 * Three processes (p, q, r)
	 * p:
	 *  init {
	 *    send<0> thru pq (a)
	 *    send<1> thru pr (b)
	 *  }
	 * q:
	 *  recv<0> thru pq (c) {}
	 *  recv<1> thru rq (d) {}
	 * r:
	 *  recv<1> thru pr (e) {
	 *    send<1> thru rq (f)
	 *  }
	 *
	 * We consider the computation where c < d,
	 * then provide possible executions in this computation
	 * and check that they all lead to the correct causal
	 * ordering of the events.
	 * We do *not* want an execution where d comes before c
	 * to also lead to this set of rules: hence the assertNotEquals at the end
	 * (see also the solution of exercise I-1b)
	 */
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

	/**
	 * constructorTest3:
	 * See exercise I-2 from the exercises on Chapter 2
	 * Three processes (p, q, r)
	 * p:
	 *  recv<1> thru qp (a) {
	 *    send<1> thru pq (b)
	 *  }
	 * q:
	 *  init {
	 *    send<0> thru qr (c)
	 *    send<1> thru qp (d)
	 *  }
	 *  recv<1> thru pq (e) {}
	 * r:
	 *  recv<0> thru qr (f) {}
	 *
	 * Same type of test as the others (here we have only 1 computation)
	 */
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

	/**
	 * constructorTest4:
	 * See exercise I-3 from the exercises on Chapter 2
	 * Three processes (p, q, r)
	 * p:
	 *  init {
	 *    send<0> thru pr (a)
	 *  }
	 * q:
	 *  init {
	 *    send<1> thru qr (b)
	 *  }
	 * r:
	 *  recv<0> thru pr (c) {}
	 *  recv<1> thru qr (d) {}
	 *
	 * Same type of test as the others
	 * (here we consider the computation with c<d)
	 */
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

	/**
	 * constructorTest5:
	 * See exercise I-3 from the exercises on Chapter 2
	 * Three processes (p, q, r)
	 * p:
	 *  init {
	 *    send<0> thru pr (a)
	 *  }
	 * q:
	 *  init {
	 *    send<1> thru qr (b)
	 *  }
	 * r:
	 *  recv<0> thru pr (c) {}
	 *  recv<1> thru qr (d) {}
	 *
	 * Same type of test as the others
	 * (here we consider the computation with d<c)
	 */
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

	/**
	 * constructorTest6:
	 * Simpler test: we just consider internal actions.
	 * One process (p) with three internal actions (a, b, c)
	 * p:
	 *  init {
	 *    a
	 *    b
	 *    c
	 *  }
	 * Because of the order of the internal actions,
	 * only one execution is possible (a, then b, then c)
	 * any other execution should *not* lead to the rules
	 * a < b, b < c.
	 */
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

	/**
	 * toComputationTest1:
	 * See again exercise I-1 from the exercises on Chapter 2
	 * Here we provide the rules and want to get the correct
	 * execution. As we consider the computation where d<c,
	 * we expect to see only the given execution as a result
	 * (see solution for I-1b)
	 */
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

	/**
	 * toComputationTest2:
	 * Same as toComputationTest1, but now with c<d.
	 * So we expect all executions of that computation
	 * to be returned.
	 * (see solution for I-1b)
	 */
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

	/**
	 * toComputationTest3:
	 * See exercise I-1c from the exercises on Chapter 2
	 * We remove a < b from the computation used in
	 * toComputationTest1, and check that the possible
	 * executions correspond with what we expect.
	 */
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

	/**
	 * toComputationTest4:
	 * Same as toComputationTest3, but with
	 * the computation from toComputationTest2
	 * (we remove a < b here too, as in exercise I-1c)
	 */
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

	/**
	 * toComputationTest5:
	 * Same as toComputationTest1 and 2, but based
	 * on exercise I-2 (see solution for I-2b)
	 */
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

	/**
	 * toComputationTest6:
	 * Same as toComputationTest3 and 4, but with
	 * the computation from toComputationTest5
	 * (we remove a < b, and add d < b and a < e,
	 * as in exercise I-2c)
	 */
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

	/**
	 * toComputationTest7:
	 * Same as toComputationTest1 and 2, but based
	 * on exercise I-3 (see solution for I-3b).
	 * Here we consider the computation with c < d.
	 */
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

	/**
	 * toComputationTest8:
	 * Same as toComputationTest7, now
	 * using the computation with d < c.
	 */
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

	/**
	 * toComputationTest9:
	 * Same as toComputationTest3 and 4, but with
	 * the computation from toComputationTest7
	 * (we remove a < c, as in exercise I-3c)
	 */
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

	/**
	 * toComputationTest10:
	 * Same as toComputationTest9, but with
	 * the computation from toComputationTest8
	 * (we remove a < c, as in exercise I-3c)
	 */
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
