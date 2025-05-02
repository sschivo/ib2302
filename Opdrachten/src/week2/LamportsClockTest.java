package week2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.Network;
import framework.Process;

/**
 * This class defines the expected results of the three
 * test cases for Lamport's Clock.
 * For the definition of the test cases, see class
 * LogicalClockTest.
 */

class LamportsClockTest extends LogicalClockTest<Integer> {

	@Override
	LogicalClock<Integer> newLogicalClock(Map<Process, List<Event>> sequences) {
		return new LamportsClock(sequences);
	}

	/**
	 * Test case 1: see the solution to exercise II-1a
	 * of the exercises on Chapter 2.
	 */
	@Override
	Map<Event, Integer> testConstructor1_expected(Network n) {
		Map<Event, Integer> expected = new HashMap<>();
		expected.put(Event.parse("a@p", n), 1);
		expected.put(Event.parse("s(p,r,1)", n), 2);
		expected.put(Event.parse("r(q,p,3)", n), 8);
		expected.put(Event.parse("b@p", n), 9);
		expected.put(Event.parse("c@q", n), 1);
		expected.put(Event.parse("r(r,q,2)", n), 6);
		expected.put(Event.parse("s(q,p,3)", n), 7);
		expected.put(Event.parse("r(p,r,1)", n), 3);
		expected.put(Event.parse("d@r", n), 4);
		expected.put(Event.parse("s(r,q,2)", n), 5);
		expected.put(Event.parse("e@r", n), 6);
		return expected;
	}

	/**
	 * Test case 2: see the solution to exercise II-2a
	 * of the exercises on Chapter 2.
	 */
	@Override
	Map<Event, Integer> testConstructor2_expected(Network n) {
		Map<Event, Integer> expected = new HashMap<>();
		expected.put(Event.parse("r(q,p,1)", n), 2);
		expected.put(Event.parse("s(p,q,2)", n), 3);
		expected.put(Event.parse("s(p,r,3)", n), 4);
		expected.put(Event.parse("s(q,p,1)", n), 1);
		expected.put(Event.parse("a@q", n), 2);
		expected.put(Event.parse("r(r,q,4)", n), 3);
		expected.put(Event.parse("b@q", n), 4);
		expected.put(Event.parse("r(p,q,2)", n), 5);
		expected.put(Event.parse("s(r,q,4)", n), 1);
		expected.put(Event.parse("c@r", n), 2);
		expected.put(Event.parse("r(p,r,3)", n), 5);
		return expected;
	}

	/**
	 * Test case 3: see the solution to exercise II-3a
	 * of the exercises on Chapter 2.
	 */
	@Override
	Map<Event, Integer> testConstructor3_expected(Network n) {
		Map<Event, Integer> expected = new HashMap<>();
		expected.put(Event.parse("r(r,p,1)", n), 2);
		expected.put(Event.parse("a@p", n), 3);
		expected.put(Event.parse("s(p,r,2)", n), 4);
		expected.put(Event.parse("r(r,p,4)", n), 5);
		expected.put(Event.parse("s(q,r,3)", n), 1);
		expected.put(Event.parse("r(r,q,5)", n), 5);
		expected.put(Event.parse("s(r,p,1)", n), 1);
		expected.put(Event.parse("s(r,p,4)", n), 2);
		expected.put(Event.parse("r(q,r,3)", n), 3);
		expected.put(Event.parse("s(r,q,5)", n), 4);
		expected.put(Event.parse("r(p,r,2)", n), 5);
		return expected;
	}
}
