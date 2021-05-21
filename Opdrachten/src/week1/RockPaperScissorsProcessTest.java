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

class RockPaperScissorsProcessTest {

	// Process should send messages on init (n = 3)
	@Test
	void initTest1() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertEquals(1, n.getChannel("p", "q").getContent().size());
		assertEquals(1, n.getChannel("p", "r").getContent().size());
	}

	// Process should send RPSmessages on init (n = 100)
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

	// Handling of proper receive events
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

	// Should throw exception on double receive from the same process (n = 2)
	@Test
	void receiveTest2() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	// Should throw exception on double receive from the same process (n = 3)
	@Test
	void receiveTest3() {
		Network n = Network.parse(false, "p,q,r:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();
		receiveOrCatch(p, new RockPaperScissorsMessage(Item.ROCK), n.getChannel("q", "p"));

		assertThrows(IllegalReceiveException.class,
				() -> p.receive(new RockPaperScissorsMessage(Item.PAPER), n.getChannel("q", "p")));
	}

	// Should throw exception on receiving illegal message type
	@Test
	void receiveTest4() {
		Network n = Network.parse(false, "p,q:week1.RockPaperScissorsProcess").makeComplete();

		Process p = n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	// Simulate a full run (n = 10)
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
