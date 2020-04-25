package week2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.Network;
import framework.Process;

class VectorClockTest extends LogicalClockTest<Map<Process, Integer>> {

	@Override
	LogicalClock<Map<Process, Integer>> newLogicalClock(Map<Process, List<Event>> sequences) {
		return new VectorClock(sequences);
	}

	@Override
	Map<Event, Map<Process, Integer>> testConstructor1_expected(Network n) {
		Map<Event, Map<Process, Integer>> expected = new HashMap<>();
		expected.put(Event.parse("a@p", n), VectorClock.parseTimestamp("1,0,0", n));
		expected.put(Event.parse("s(p,r,1)", n), VectorClock.parseTimestamp("2,0,0", n));
		expected.put(Event.parse("r(q,p,3)", n), VectorClock.parseTimestamp("3,3,3", n));
		expected.put(Event.parse("b@p", n), VectorClock.parseTimestamp("4,3,3", n));
		expected.put(Event.parse("c@q", n), VectorClock.parseTimestamp("0,1,0", n));
		expected.put(Event.parse("r(r,q,2)", n), VectorClock.parseTimestamp("2,2,3", n));
		expected.put(Event.parse("s(q,p,3)", n), VectorClock.parseTimestamp("2,3,3", n));
		expected.put(Event.parse("r(p,r,1)", n), VectorClock.parseTimestamp("2,0,1", n));
		expected.put(Event.parse("d@r", n), VectorClock.parseTimestamp("2,0,2", n));
		expected.put(Event.parse("s(r,q,2)", n), VectorClock.parseTimestamp("2,0,3", n));
		expected.put(Event.parse("e@r", n), VectorClock.parseTimestamp("2,0,4", n));
		return expected;
	}
	
	@Override
	Map<Event, Map<Process, Integer>> testConstructor2_expected(Network n) {
		Map<Event, Map<Process, Integer>> expected = new HashMap<>();
		expected.put(Event.parse("r(q,p,1)", n), VectorClock.parseTimestamp("1,1,0", n));
		expected.put(Event.parse("s(p,q,2)", n), VectorClock.parseTimestamp("2,1,0", n));
		expected.put(Event.parse("s(p,r,3)", n), VectorClock.parseTimestamp("3,1,0", n));
		expected.put(Event.parse("s(q,p,1)", n), VectorClock.parseTimestamp("0,1,0", n));
		expected.put(Event.parse("a@q", n), VectorClock.parseTimestamp("0,2,0", n));
		expected.put(Event.parse("r(r,q,4)", n), VectorClock.parseTimestamp("0,3,1", n));
		expected.put(Event.parse("b@q", n), VectorClock.parseTimestamp("0,4,1", n));
		expected.put(Event.parse("r(p,q,2)", n), VectorClock.parseTimestamp("2,5,1", n));
		expected.put(Event.parse("s(r,q,4)", n), VectorClock.parseTimestamp("0,0,1", n));
		expected.put(Event.parse("c@r", n), VectorClock.parseTimestamp("0,0,2", n));
		expected.put(Event.parse("r(p,r,3)", n), VectorClock.parseTimestamp("3,1,3", n));
		return expected;
	}
	
	@Override
	Map<Event, Map<Process, Integer>> testConstructor3_expected(Network n) {
		Map<Event, Map<Process, Integer>> expected = new HashMap<>();
		expected.put(Event.parse("r(r,p,1)", n), VectorClock.parseTimestamp("1,0,1", n));
		expected.put(Event.parse("a@p", n), VectorClock.parseTimestamp("2,0,1", n));
		expected.put(Event.parse("s(p,r,2)", n), VectorClock.parseTimestamp("3,0,1", n));
		expected.put(Event.parse("r(r,p,4)", n), VectorClock.parseTimestamp("4,0,2", n));
		expected.put(Event.parse("s(q,r,3)", n), VectorClock.parseTimestamp("0,1,0", n));
		expected.put(Event.parse("r(r,q,5)", n), VectorClock.parseTimestamp("0,2,4", n));
		expected.put(Event.parse("s(r,p,1)", n), VectorClock.parseTimestamp("0,0,1", n));
		expected.put(Event.parse("s(r,p,4)", n), VectorClock.parseTimestamp("0,0,2", n));
		expected.put(Event.parse("r(q,r,3)", n), VectorClock.parseTimestamp("0,1,3", n));
		expected.put(Event.parse("s(r,q,5)", n), VectorClock.parseTimestamp("0,1,4", n));
		expected.put(Event.parse("r(p,r,2)", n), VectorClock.parseTimestamp("3,1,5", n));
		return expected;
	}
}
