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
 * Tests the base version of the Rock-Paper-Scissors protocol.
 */

class RockPaperScissorsProcessTest {

	/**
	 * initTest1:
	 * Verifies that when running with 3 players (p, q, r),
	 * process p broadcasts exactly one RockPaperScissorsMessage to each other player upon initialization.
	 * Checks that each outgoing channel from p contains exactly one message.
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertEquals(1, n.getChannel("p", "q").getContent().size());
		assertEquals(1, n.getChannel("p", "r").getContent().size());
	}

	/**
	 * initTest2:
	 * Verifies that in a network of 100 processes, the starting process (p0)
	 * broadcasts one RockPaperScissorsMessage to each of the other 99 processes on init.
	 * Ensures the outgoing channels from p0 each contain exactly one message of the correct type.
	 */
	@Test
	void initTest2() {
		Network n = new Network(false);
		for (int i = 0; i < 100; i++) {
			n.addProcess("p" + i, "week1.RockPaperScissorsProcess");
		}
		n.makeComplete();

		Process p = n.getProcess("p0");
		p.init();

		for (int i = 1; i < 100; i++) {
			Collection<Message> content = n.getChannel("p0", "p" + i).getContent();
			assertEquals(1, content.size());
			assertTrue(content.iterator().next() instanceof RockPaperScissorsMessage);
		}
	}

	/**
	 * receiveTest1:
	 * After initialization in a 4-player game (p, q, r, s),
	 * receiving a single RockPaperScissorsMessage from one peer should not yet trigger output,
	 * because the process waits to receive one message from all other processes before deciding.
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(false, "p,q,r,s:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		// p receives from q: should not print yet (wait for r,s)
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(0, getPrinted(p).size());

		// p receives from r: wait for s
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(0, getPrinted(p).size());

		// p receives from s: should print "true true" (received three different items)
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		List<String> printed = getPrinted(p);
		assertEquals(1, printed.size());
		assertEquals("true true", printed.get(0));
	}

	/**
	 * receiveTest2:
	 * In a 2-player game (p, q), after p receives one valid message from q,
	 * a second attempt to receive another message from q should result in an IllegalReceiveException,
	 * preventing duplicate receives from the same sender.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest3:
	 * In a 3-player game (p, q, r), after p receives one valid message from q,
	 * attempting to receive another message from q again should throw IllegalReceiveException,
	 * ensuring each sender is accepted only once per round.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest4:
	 * In a 2-player game (p, q), attempts to receive a dummy (invalid) Message type
	 * should throw IllegalReceiveException immediately,
	 * enforcing strict type checking of incoming messages.
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	/**
	 * simulationTest1:
	 * Runs a full game simulation among 10 processes labeled p0–p9,
	 * each choosing and then receiving all others' moves.
	 * Expects the overall simulation to terminate successfully and each process to output exactly one decision.
	 * Verifies that each printed result is one of the four possible outcomes: true/false combinations.
	 */
	@Test
	void simulationTest1() {
		Network n = new Network(false);
		for (int i = 0; i < 10; i++) {
			n.addProcess("p" + i, "week1.RockPaperScissorsProcess");
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

		// Should have terminated and printed one of the four prescribed outputs
		for (int i = 0; i < 10; i++) {
			assertEquals(1, output.get("p" + i).size());
			assertTrue(res.contains(output.get("p" + i).iterator().next()));
		}
	}
}
