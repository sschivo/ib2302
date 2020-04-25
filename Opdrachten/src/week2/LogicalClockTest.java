package week2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import framework.Network;
import framework.Process;

abstract class LogicalClockTest<T> {

	@Test
	void testConstructor1() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$a", "a@p");
		substitutions.put("$s1", "s(p,r,1)");
		substitutions.put("$r3", "r(q,p,3)");
		substitutions.put("$b", "b@p");
		substitutions.put("$c", "c@q");
		substitutions.put("$r2", "r(r,q,2)");
		substitutions.put("$s3", "s(q,p,3)");
		substitutions.put("$r1", "r(p,r,1)");
		substitutions.put("$d", "d@r");
		substitutions.put("$s2", "s(r,q,2)");
		substitutions.put("$e", "e@r");

		Network n = Network.parse(true, "p,q,r:framework.DefaultProcess");
		
		Map<Process, List<Event>> sequences = new HashMap<>();
		sequences.put(n.getProcess("p"), Event.parseList(sub("$a $s1 $r3 $b", substitutions), n));
		sequences.put(n.getProcess("q"), Event.parseList(sub("$c $r2 $s3", substitutions), n));
		sequences.put(n.getProcess("r"), Event.parseList(sub("$r1 $d $s2 $e", substitutions), n));
		
		assertEquals(testConstructor1_expected(n), newLogicalClock(sequences).getTimestamps());
	}
	
	@Test
	void testConstructor2() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$r1", "r(q,p,1)");
		substitutions.put("$s2", "s(p,q,2)");
		substitutions.put("$s3", "s(p,r,3)");
		substitutions.put("$s1", "s(q,p,1)");
		substitutions.put("$a", "a@q");
		substitutions.put("$r4", "r(r,q,4)");
		substitutions.put("$b", "b@q");
		substitutions.put("$r2", "r(p,q,2)");
		substitutions.put("$s4", "s(r,q,4)");
		substitutions.put("$c", "c@r");
		substitutions.put("$r3", "r(p,r,3)");

		Network n = Network.parse(true, "p,q,r:framework.DefaultProcess");
		
		Map<Process, List<Event>> sequences = new HashMap<>();
		sequences.put(n.getProcess("p"), Event.parseList(sub("$r1 $s2 $s3", substitutions), n));
		sequences.put(n.getProcess("q"), Event.parseList(sub("$s1 $a $r4 $b $r2", substitutions), n));
		sequences.put(n.getProcess("r"), Event.parseList(sub("$s4 $c $r3", substitutions), n));
		
		assertEquals(testConstructor2_expected(n), newLogicalClock(sequences).getTimestamps());
	}
	
	@Test
	void testConstructor3() {
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("$r1", "r(r,p,1)");
		substitutions.put("$a", "a@p");
		substitutions.put("$s2", "s(p,r,2)");
		substitutions.put("$r4", "r(r,p,4)");
		substitutions.put("$s3", "s(q,r,3)");
		substitutions.put("$r5", "r(r,q,5)");
		substitutions.put("$s1", "s(r,p,1)");
		substitutions.put("$s4", "s(r,p,4)");
		substitutions.put("$r3", "r(q,r,3)");
		substitutions.put("$s5", "s(r,q,5)");
		substitutions.put("$r2", "r(p,r,2)");
		
		Network n = Network.parse(true, "p,q,r:framework.DefaultProcess");
		
		Map<Process, List<Event>> sequences = new HashMap<>();
		sequences.put(n.getProcess("p"), Event.parseList(sub("$r1 $a $s2 $r4", substitutions), n));
		sequences.put(n.getProcess("q"), Event.parseList(sub("$s3 $r5", substitutions), n));
		sequences.put(n.getProcess("r"), Event.parseList(sub("$s1 $s4 $r3 $s5 $r2", substitutions), n));
		
		assertEquals(testConstructor3_expected(n), newLogicalClock(sequences).getTimestamps());
	}

	private String sub(String s, Map<String, String> substitutions) {
		for (Entry<String, String> substitution : substitutions.entrySet()) {
			s = s.replace(substitution.getKey(), substitution.getValue());
		}
		return s;
	}

	abstract LogicalClock<T> newLogicalClock(Map<Process, List<Event>> sequences);

	abstract Map<Event, T> testConstructor1_expected(Network n);
	
	abstract Map<Event, T> testConstructor2_expected(Network n);
	
	abstract Map<Event, T> testConstructor3_expected(Network n);
}
