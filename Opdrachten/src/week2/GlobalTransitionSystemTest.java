package week2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class GlobalTransitionSystemTest {

	@Test
	void hasExecutionTest1() {
		String s = "g0 ";
		s += "g0--a@p->g1 g1--b@p->g2 g2--e@r->g3 g3--f@r->g4 g4--d@q->g5 ";
		s += "g6--b@p->g7 g7--e@r->g8 g8--f@r->g9 g9--d@q->g10 ";
		s += "g1--c@q->g6 g2--c@q->g7 g3--c@q->g8 g4--c@q->g9 g5--c@q->g10";

		Map<String, Configuration> configurations = new HashMap<>();

		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g7 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g6 g7 g8 g9 g10", configurations)));
		
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g1 g2 g3 g4 g5 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g11 g10", configurations)));
	}
	
	@Test
	void hasExecutionTest2() {
		String s = "g0 ";
		s += "g0--c@q->g1 g1--d@q->g2 g2--a@p->g3 g3--b@p->g4 g4--e@q->g5 ";
		s += "g6--d@q->g7 g7--a@p->g8 g8--b@p->g9 g9--e@q->g10 ";
		s += "g1--f@r->g6 g2--f@r->g7 g3--f@r->g8 g4--f@r->g9 g5--f@r->g10";

		Map<String, Configuration> configurations = new HashMap<>();

		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g7 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g6 g7 g8 g9 g10", configurations)));
		
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g1 g2 g3 g4 g5 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g11 g10", configurations)));
	}
	
	@Test
	void hasExecutionTest3() {
		String s = "g0 ";
		s += "g0--a@p->g1 g1--c@r->g2 ";
		s += "g3--a@p->g4 g4--c@r->g5 ";
		s += "g6--a@p->g7 g7--c@r->g8 ";
		s += "g0--b@q->g3 g1--b@q->g4 g2--b@q->g5 ";
		s += "g3--d@r->g6 g4--d@r->g7 g5--d@r->g8";
		
		Map<String, Configuration> configurations = new HashMap<>();

		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g5 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g4 g5 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g4 g7 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g3 g4 g5 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g3 g4 g7 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g3 g6 g7 g8", configurations)));
		
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g1 g2 g5 g8", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g5", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g8", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g5 g9 g8", configurations)));
	}

	@Test
	void hasExecutionTest4() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 3, 4, 12);
		for (int i = 0; i < 10; i++) {
			assertTrue(system.hasExecution(system.randomExecution(i)));
		}
	}

	@Test
	void hasExecutionTest5() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 30, 40, 120);
		for (int i = 0; i < 10; i++) {
			assertTrue(system.hasExecution(system.randomExecution(i)));
		}
	}

	@Test
	void hasExecutionTest6() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 3, 4, 12);
		for (int i = 0; i < 10; i++) {
			assertFalse(system.hasExecution(system.randomNonExecution(i)));
		}
	}

	@Test
	void hasExecutionTest7() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 30, 40, 120);
		for (int i = 0; i < 100; i++) {
			assertFalse(system.hasExecution(system.randomNonExecution(i)));
		}
	}
	
	@Test
	void hasExecutionTest8() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 30, 40, 120);
		assertFalse(system.hasExecution(new ArrayList<>()));
	}

//	@Test
//	void parseTest1() {
//		String s = "g0 g0--a@p->g1";
//		Map<String, Configuration> configurations = new LinkedHashMap<>();
//
//		assertEquals(s, GlobalTransitionSystem.parse(s, configurations).toString(configurations));
//	}
//
//	@Test
//	void parseTest2() {
//		String s = "g0 g0--s(p,q,1)->g1 g1--r(p,q,1)->g2 g3--a@q->g0";
//		Map<String, Configuration> configurations = new LinkedHashMap<>();
//
//		assertEquals(s, GlobalTransitionSystem.parse(s, configurations).toString(configurations));
//	}
//
//	@Test
//	void parseTest3() {
//		String s = "g0 g0--s(p,q,1)->g1 g1--r(p,q,1)->g2 g1--s(p,q,2)->g4 g3--a@q->g0";
//		Map<String, Configuration> configurations = new LinkedHashMap<>();
//
//		assertEquals(s, GlobalTransitionSystem.parse(s, configurations).toString(configurations));
//	}
}
