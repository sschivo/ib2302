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

class RockPaperScissorsMultiRoundsProcessTest {

	// Should send a message to every opponent on init
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p,q,r,s:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertEquals(1, n.getChannel("p", "q").getContent().size());
		assertEquals(1, n.getChannel("p", "r").getContent().size());
		assertEquals(1, n.getChannel("p", "s").getContent().size());
	}

	// Should throw exception on receiving illegal message type
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p,q:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	// Should send a second message (start second round) after receiving from every opponent (and not losing)
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

	// Can handle messages from one round ahead
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

	// Should play multiple rounds (n = 2)
	// Test returns p's own message every time, so p and q play a draw
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

	// Should not receive messages from two rounds ahead
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p,q,r:week1.RockPaperScissorsMultiRoundsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p")));
	}

	// Should only start a new round after receiving from all opponents (n = 4, three rounds)
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

	// Same as receiveTest6 but different order
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

	// Should not receive messages from two rounds ahead (larger example)
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

	// Same as receiveTest6 but different order and with a third-round message
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

	// Force loss and check that player stops participating (i.e., only plays draws) (n = 2)
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

	// Force win and assert termination (n = 2)
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

	// Simulate a full run
	// (Note that the test will also fail if there are only draws for 10000 iterations or so, but the probability of this happening is negligable.)
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
