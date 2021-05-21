package week1;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Network;
import framework.Process;

class RockPaperScissorsCheatingProcessTest {

	// Outgoing channels should be empty after initialisation
	@Test
	void initTest1() {
		Network n = Network.parse(false, "p,q,r,s:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertEquals(0, n.getChannel("p", "q").getContent().size());
		assertEquals(0, n.getChannel("p", "r").getContent().size());
		assertEquals(0, n.getChannel("p", "s").getContent().size());
	}

	// Should throw exception on receiving twice from the same process (n = 2)
	@Test
	void receiveTest1() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	// Should throw exception on receiving twice from the same process (n = 3)
	@Test
	void receiveTest2() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	// Should throw exception on receiving illegal message type
	@Test
	void receiveTest3() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	// When receiving ROCK (n = 2), should return PAPER and win
	@Test
	void receiveTest4() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		Collection<Message> content = n.getChannel("p", "q").getContent();
		assertEquals(1, content.size());
		assertEquals(Item.PAPER, ((RockPaperScissorsMessage) content.iterator().next()).getItem());

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertEquals("true false", printed.get(0));
	}

	// When receiving PAPER (n = 2), should return SCISSORS and win
	@Test
	void receiveTest5() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p"));
		Collection<Message> content = n.getChannel("p", "q").getContent();
		assertEquals(1, content.size());
		assertEquals(Item.SCISSORS, ((RockPaperScissorsMessage) content.iterator().next()).getItem());

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertEquals("true false", printed.get(0));
	}

	// When receiving SCISSORS (n = 2), should return ROCK and win
	@Test
	void receiveTest6() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("q", "p"));
		Collection<Message> content = n.getChannel("p", "q").getContent();
		assertEquals(1, content.size());
		assertEquals(Item.ROCK, ((RockPaperScissorsMessage) content.iterator().next()).getItem());

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertEquals("true false", printed.get(0));
	}

	// When receiving ROCK and PAPER (n = 3), should return anything but ROCK and thus not lose
	@Test
	void receiveTest7() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		Collection<Message> content = n.getChannel("p", "q").getContent();
		assertEquals(0, content.size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		content = n.getChannel("p", "q").getContent();
		assertEquals(1, content.size());
		assertFalse(((RockPaperScissorsMessage) content.iterator().next()).getItem() == Item.ROCK);

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertFalse("false true" == printed.get(0));
	}

	// When receiving ROCK and SCISSORS (n = 3), should return anything but SCISSORS and thus not lose
	@Test
	void receiveTest8() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		Collection<Message> content = n.getChannel("p", "q").getContent();
		assertEquals(0, content.size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("r", "p"));
		content = n.getChannel("p", "q").getContent();
		assertEquals(1, content.size());
		assertFalse(((RockPaperScissorsMessage) content.iterator().next()).getItem() == Item.SCISSORS);

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertFalse("false true" == printed.get(0));
	}

	// When receiving SCISSORS and PAPER (n = 3), should return anything but PAPER and thus not lose
	@Test
	void receiveTest9() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("q", "p"));
		Collection<Message> content = n.getChannel("p", "q").getContent();
		assertEquals(0, content.size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		content = n.getChannel("p", "q").getContent();
		assertEquals(1, content.size());
		assertFalse(((RockPaperScissorsMessage) content.iterator().next()).getItem() == Item.PAPER);

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertFalse("false true" == printed.get(0));
	}

	// When receiving ROCK, PAPER and SCISSORS (n = 4), should not win or lose
	@Test
	void receiveTest10() {
		Network n = Network.parse(false, "p,q,r,s:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));

		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertEquals("true true", printed.get(0));
	}

	// Should only send messages after receiving from all opponents
	@Test
	void receiveTest11() {
		Network n = Network.parse(false, "p,q,r,s:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(0, n.getChannel("p", "q").getContent().size());
		assertEquals(0, n.getChannel("p", "r").getContent().size());
		assertEquals(0, n.getChannel("p", "s").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(0, n.getChannel("p", "q").getContent().size());
		assertEquals(0, n.getChannel("p", "r").getContent().size());
		assertEquals(0, n.getChannel("p", "s").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		assertEquals(1, n.getChannel("p", "r").getContent().size());
		assertEquals(1, n.getChannel("p", "s").getContent().size());
	}

	// Simulate full run (n = 7)
	@Test
	void simulationTest1() {
		Network n = Network.parse(false, "p:week1.RockPaperScissorsCheatingProcess");
		for (int i = 0; i < 6; i++) {
			n.addProcess("q" + i, "week1.RockPaperScissorsProcess");
		}
		n.makeComplete();
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();
		Set<String> res = new HashSet<String>();
		res.add("true true");
		res.add("true false");
		res.add("false true");
		res.add("false false");

		try {
			assertTrue(n.simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		// All processes should terminate and give some proper output, and p should not lose
		for (int i = 0; i < 6; i++) {
			assertEquals(1, output.get("q" + i).size());
			assertTrue(res.contains(output.get("q" + i).iterator().next()));
		}
		assertEquals(1, output.get("p").size());
		String pres = output.get("p").iterator().next();
		assertTrue(res.contains(pres));
		assertNotEquals("false false", pres);
		assertNotEquals("false true", pres);
	}

	// Simulate full run (n = 4, two cheaters)
	@Test
	void simulationTest2() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess r,s:week1.RockPaperScissorsProcess").makeComplete();
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(n.simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		// Should terminate without output (p and q wait for each other)
		assertEquals(0, output.get("p").size());
		assertEquals(0, output.get("q").size());
		assertEquals(0, output.get("r").size());
		assertEquals(0, output.get("s").size());
	}
}
