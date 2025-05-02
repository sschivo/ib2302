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

/**
 * Tests the cheating variant: each process adapts its next move to beat
 * the majority of opponents' last choices.
 */

class RockPaperScissorsCheatingProcessTest {

	/**
	 * initTest1:
	 * At start (n=2), before any receives, the cheating process should
	 * not send any messages by itself.
	 * Verifies all outgoing channels are initially empty until first receive.
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(false, "p,q,r,s:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertEquals(0, n.getChannel("p", "q").getContent().size());
		assertEquals(0, n.getChannel("p", "r").getContent().size());
		assertEquals(0, n.getChannel("p", "s").getContent().size());
	}

	/**
	 * receiveTest1:
	 * In a 2-player game, two receives from the same peer are illegal.
	 * Expects IllegalReceiveException on the second receive() call.
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest2:
	 * Same as receiveTest1 but with 3 players in the network.
	 * Confirms duplicate receives from one sender are always rejected,
	 * regardless of total player count.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest3:
	 * Receiving an unsupported Message type should be rejected immediately.
	 * Expects IllegalReceiveException when passing Message.DUMMY.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsCheatingProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	/**
	 * receiveTest4:
	 * In a 2-player match where q first plays ROCK,
	 * the cheating process should respond with PAPER (win), then terminate.
	 * Checks that exactly one message is sent and p prints "true false".
	 */
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

	/**
	 * receiveTest5:
	 * Similar to receiveTest4 but when q plays PAPER,
	 * p should counter with SCISSORS (win) and then terminate.
	 */
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

	/**
	 * receiveTest6:
	 * Similar to receiveTest4 but when q plays SCISSORS,
	 * p should counter with ROCK (win) and then terminate.
	 */
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

	/**
	 * receiveTest7:
	 * In a 3-player game, if q and r play ROCK and PAPER,
	 * p should choose a move that doesn't lose (anything but ROCK).
	 * Verifies p remains in the game when a clear winning move is unavailable.
	 */
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

	/**
	 * receiveTest8:
	 * In a 3-player game, if opponents play ROCK and SCISSORS,
	 * p should not pick SCISSORS (to avoid losing).
	 */
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

	/**
	 * receiveTest9:
	 * In a 3-player game, if opponents play SCISSORS and PAPER,
	 * p avoids PAPER to prevent losing.
	 */
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

	/**
	 * receiveTest10:
	 * In a 4-player game (ROCK, PAPER, SCISSORS from q,r,s),
	 * no single move guarantees a win.
	 * Expects p to pick any move and finish without losing.
	 */
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

	/**
	 * receiveTest11:
	 * Verifies that p only begins cheating/responding
	 * after it has received exactly one move from every opponent.
	 * Channels should remain empty until the final receive triggers reply.
	 */
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

	/**
	 * simulationTest1:
	 * Simulates one cheater (p) against 6 honest players.
	 * Expects all honest players to terminate with some result,
	 * and p to never lose ("false false" or "false true" are prohibited).
	 */
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

	/**
	 * simulationTest2:
	 * Simulates two cheaters (p and q) among 4 total processes.
	 * Since the cheaters wait on each other indefinitely,
	 * the protocol should deadlock and produce no output.
	 */
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
