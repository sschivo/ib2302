package week1;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Network;
import framework.Process;
import framework.ListChannel;

/**
 * Tests the multi-round Rock-Paper-Scissors protocol.
 */

class RockPaperScissorsMultiRoundsProcessTest {

	/**
	 * initTest1:
	 * In a 4-player game (p, q, r, s), upon init() each player should
	 * send exactly one RockPaperScissorsMessage to every other participant.
	 * Verifies that p's outgoing channels to q, r, s each contain exactly one message.
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertEquals(1, n.getChannel("p", "q").getContent().size());
		assertEquals(1, n.getChannel("p", "r").getContent().size());
		assertEquals(1, n.getChannel("p", "s").getContent().size());
	}

	/**
	 * receiveTest1:
	 * A dummy/invalid message should be rejected.
	 * Expects IllegalReceiveException if p.receive() is called with Message.DUMMY.
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p,q:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	/**
	 * receiveTest2:
	 * After receiving one move from each opponent in round 1 (n=4),
	 * p should automatically start round 2 by broadcasting again.
	 * Confirms p's channel to q grows from size 1 to 2 only once all three messages have arrived.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());
	}

	/**
	 * receiveTest3:
	 * Ensures that if a peer sends its round-2 message early (before p has finished round 1),
	 * p still waits for all of round 1 before broadcasting round 2.
	 * The extra early message must be buffered but not trigger a second broadcast too soon.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		// message from second round of q: should not start second round of p yet
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());
	}

	/**
	 * receiveTest4:
	 * Verifies that in a 2-player match the protocol handles multiple rounds correctly.
	 * We use mirror p's move to get a draw and go to the next round.
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p,q:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		ListChannel pq = (ListChannel) n.getChannel("p", "q");

		Item item = ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(0)).getItem();
		receiveOrCatch(p, new RockPaperScissorsMessage(item), n.getChannel("q", "p"));
		assertEquals(2, pq.getContent().size());

		item = ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(1)).getItem();
		receiveOrCatch(p, new RockPaperScissorsMessage(item), n.getChannel("q", "p"));
		assertEquals(3, pq.getContent().size());

		item = ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(2)).getItem();
		receiveOrCatch(p, new RockPaperScissorsMessage(item), n.getChannel("q", "p"));
		assertEquals(4, pq.getContent().size());
	}

	/**
	 * receiveTest5:
	 * Checks that messages from two full rounds ahead are considered invalid.
	 * Expects IllegalReceiveException if p receives a third round message before completing round 2.
	 */
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p,q,r:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest6:
	 * In a 4-player setting over three rounds, p should not start round 2 or 3
	 * until it has received exactly one message from each other process in the previous round.
	 * Verifies the sequencing of broadcasts across multiple rounds.
	 */
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(3, n.getChannel("p", "q").getContent().size());
	}

	/**
	 * receiveTest7:
	 * Same as receiveTest6 but with a different arrival order of messages.
	 * Ensures ordering of receives does not affect the correctness of round transitions.
	 */
	@Test
	void receiveTest7() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(3, n.getChannel("p", "q").getContent().size());
	}

	/**
	 * receiveTest8:
	 * A larger 4-player/example where one peer's second-round message arrives early.
	 * Verifies p still follows the rule of waiting for all round-1 messages before round 2.
	 */
	@Test
	void receiveTest8() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest9:
	 * Extends receiveTest6 by including a third round message in the mix.
	 * Checks that p defers round 3 until it has legitimately finished round 2 with all peers.
	 */
	@Test
	void receiveTest9() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("r", "p"));
		assertEquals(1, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(2, n.getChannel("p", "q").getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("s", "p"));
		assertEquals(3, n.getChannel("p", "q").getContent().size());
	}

	/**
	 * passiveTest1:
	 * Forces a "loss" in a 2-player game by picking the losing response each time.
	 * After losing, p should continue playing only draws (i.e., never win or lose again).
	 * Verifies p's outgoing messages to q remain draws indefinitely.
	 */
	@Test
	void passiveTest1() {
		Network n = Network.parse(true, "p,q:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		ListChannel pq = (ListChannel) n.getChannel("p", "q");
		Item opponentItem = ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(0)).getItem();

		Item item = Item.ROCK;
		if (opponentItem == Item.ROCK) {
			item = Item.PAPER;
		} else if (opponentItem == Item.PAPER) {
			item = Item.SCISSORS;
		}

		receiveOrCatch(p, new RockPaperScissorsMessage(item), n.getChannel("q", "p"));
		assertEquals(1, pq.getContent().size());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertEquals(2, pq.getContent().size());
		assertEquals(Item.ROCK, ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(1)).getItem());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p"));
		assertEquals(3, pq.getContent().size());
		assertEquals(Item.PAPER, ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(2)).getItem());

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.SCISSORS), n.getChannel("q", "p"));
		assertEquals(4, pq.getContent().size());
		assertEquals(Item.SCISSORS, ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(3)).getItem());
	}

	/**
	 * terminationTest1:
	 * Forces a "win" in a 2-player game by picking the winning move first.
	 * p should detect its victory and cease participating.
	 * Verifies no further messages are sent after the winning receive.
	 */
	@Test
	void terminationTest1() {
		Network n = Network.parse(true, "p,q:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		ListChannel pq = (ListChannel) n.getChannel("p", "q");
		Item opponentItem = ((RockPaperScissorsMessage) ((ArrayList<Message>) pq.getContent()).get(0)).getItem();

		Item item = Item.ROCK;
		if (opponentItem == Item.ROCK) {
			item = Item.SCISSORS;
		} else if (opponentItem == Item.SCISSORS) {
			item = Item.PAPER;
		}

		receiveOrCatch(p, new RockPaperScissorsMessage(item), n.getChannel("q", "p"));
		assertEquals(1, pq.getContent().size());
	}

	/**
	 * simulationTest1:
	 * Runs a complete simulation with 6 players in true-random mode.
	 * Expects the protocol to complete (simulate() returns true) and each process
	 * terminates with only "true" or "false" in the output.
	 */
	@Test
	void simulationTest1() {
		Network n = new Network(true);
		for (int i = 0; i < 6; i++) {
			n.addProcess("p" + i, "week1.RockPaperScissorsMultiRoundsProcess");
		}
		n.makeComplete();
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();
		Set<String> res = new HashSet<String>();
		res.add("true");
		res.add("false");

		try {
			assertTrue(n.simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		System.out.println(output.toString());

		for (int i = 0; i < 6; i++) {
			assertEquals(1, output.get("p" + i).size());
			assertTrue(res.contains(output.get("p" + i).iterator().next()));
		}
	}
}
