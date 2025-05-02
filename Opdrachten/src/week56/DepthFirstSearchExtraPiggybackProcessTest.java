package week56;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Channel;
import framework.Network;

class DepthFirstSearchExtraPiggybackProcessTest {

	/**
	 * initTest1:
	 * On init() the initiator should send a TOKEN piggybacking its own ID
	 * to one neighbor and remain active.
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertTrue(p.isActive());
		assertFalse(p.isPassive());

		int sum = 0;
		Collection<Message> pout;
		for (Channel d : p.getOutgoing()) {
			pout = d.getContent();
			if (pout.size() > 0) {
				Message m = pout.iterator().next();
				assertTrue(m instanceof TokenWithIdsMessage);

				Set<String> visited = ((TokenWithIdsMessage) m).getIds();
				assertEquals(1, visited.size());
				assertTrue(visited.contains("p"));

				sum += pout.size();
			}
		}
		assertEquals(1, sum);
	}

	/**
	 * initTest2:
	 * Non-initiator should not finish and should not send anything on init
	 */
	@Test
	void initTest2() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());
		assertFalse(q.isPassive());

		for (Channel d : q.getOutgoing()) {
			assertEquals(0, d.getContent().size());
		}
	}

	/**
	 * receiveTest1:
	 * The initiator receives an illegal message type: throw exception
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	/**
	 * receiveTest2:
	 * A non-initiator receives an illegal message type: throw exception
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	/**
	 * receiveTest3:
	 * The initiator receives a TOKEN when finished: throw exception
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("q");
		m.addId("r");
		m.addId("s");

		receiveOrCatch(p, m, n.getChannel("q", "p"));
		assertTrue(p.isPassive());

		assertThrows(IllegalReceiveException.class, () -> p.receive(m, n.getChannel("q", "p")));
	}

	/**
	 * receiveTest4:
	 * A non-initiator receives a TOKEN when finished: throw exception
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("q");
		m.addId("r");
		m.addId("s");

		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertTrue(q.isPassive());

		assertThrows(IllegalReceiveException.class, () -> q.receive(m, n.getChannel("p", "q")));
	}

	/**
	 * receiveTest5:
	 * On first valid TOKEN receipt, non-initiator starts DFS,
	 * piggybacks its ID on the outgoing TOKEN.
	 */
	@Test
	void receiveTest5() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("r");

		receiveOrCatch(q, m, n.getChannel("r", "q"));

		assertTrue(q.isActive());
	}

	/**
	 * receiveTest6:
	 * A non-initiator should piggyback its ID,
	 * and send the TOKEN to a neighbour
	 * (we don't check here if visited or not).
	 */
	@Test
	void receiveTest6() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("r");

		receiveOrCatch(q, m, n.getChannel("r", "q"));

		// Forwards to someone, no matter who (other test) with added ID
		Collection<Message> qout;
		for (Channel d : q.getOutgoing()) {
			qout = d.getContent();
			if (qout.size() > 0) {
				Message m2 = qout.iterator().next();
				assertTrue(m2 instanceof TokenWithIdsMessage);

				Set<String> visited = ((TokenWithIdsMessage) m2).getIds();
				assertEquals(3, visited.size());
				assertTrue(visited.contains("p"));
				assertTrue(visited.contains("r"));
				assertTrue(visited.contains("q"));
			}
		}
	}

	/**
	 * receiveTest7:
	 * Differently from receiveTest6, here we do check that the TOKEN is sent
	 * to a neighbour that has not been visited yet.
	 */
	@Test
	void receiveTest7() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		// p may send to an arbitrary process, so check which one
		int pqsize = n.getChannel("p", "q").getContent().size();
		int prsize = n.getChannel("p", "r").getContent().size();
		int pssize = n.getChannel("p", "s").getContent().size();

		// If p sent to q or r, return token with IDs p,q,r: p should forward to s
		if (pqsize == 1 || prsize == 1) {
			TokenWithIdsMessage m = new TokenWithIdsMessage("p");
			m.addId("q");
			m.addId("r");

			receiveOrCatch(p, m, n.getChannel("q", "p"));
			assertEquals(pqsize, n.getChannel("p", "q").getContent().size());
			assertEquals(prsize, n.getChannel("p", "r").getContent().size());
			assertEquals(pssize + 1, n.getChannel("p", "s").getContent().size());

			assertTrue(n.getChannel("p", "s").getContent().toArray()[0] instanceof TokenWithIdsMessage);
			Set<String> visited = ((TokenWithIdsMessage) n.getChannel("p", "s").getContent().toArray()[0]).getIds();
			assertEquals(3, visited.size());
			assertTrue(visited.contains("p"));
			assertTrue(visited.contains("q"));
			assertTrue(visited.contains("r"));
		}

		// If p sent to s, return token with IDs p,q,s: p should forward to r
		if (pssize == 1) {
			TokenWithIdsMessage m = new TokenWithIdsMessage("p");
			m.addId("q");
			m.addId("s");

			receiveOrCatch(p, m, n.getChannel("q", "p"));
			assertEquals(pqsize, n.getChannel("p", "q").getContent().size());
			assertEquals(pssize, n.getChannel("p", "s").getContent().size());
			assertEquals(prsize + 1, n.getChannel("p", "r").getContent().size());

			assertTrue(n.getChannel("p", "r").getContent().toArray()[0] instanceof TokenWithIdsMessage);
			Set<String> visited = ((TokenWithIdsMessage) n.getChannel("p", "r").getContent().toArray()[0]).getIds();
			assertEquals(3, visited.size());
			assertTrue(visited.contains("p"));
			assertTrue(visited.contains("q"));
			assertTrue(visited.contains("s"));
		}
	}

	/**
	 * receiveTest8:
	 * Non-initiators should forward to unvisited processes,
	 * if any non-parent processes have not yet been visited.
	 */
	@Test
	void receiveTest8() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("r");

		// Parent is set to p and token contains IDs p,r: q should forward to s
		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
		assertEquals(1, n.getChannel("q", "s").getContent().size());
	}

	/**
	 * receiveTest9:
	 * Only if all neighbours have already been visited,
	 * return the TOKEN to the parent.
	 */
	@Test
	void receiveTest9() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("r");
		m.addId("s");

		Channel qp = n.getChannel("q", "p");
		assertEquals(0, qp.getContent().size());

		// Sets p as the parent. Token contains IDs p,r,s: q should return to p.
		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertEquals(1, qp.getContent().size());
		assertTrue(qp.getContent().iterator().next() instanceof TokenWithIdsMessage);
	}

	/**
	 * receiveTest10:
	 * When the exploration is complete, the initiator should finish normally
	 * and not send any other control message.
	 */
	@Test
	void receiveTest10() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertFalse(p.isPassive());

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("q");
		m.addId("r");
		m.addId("s");

		int pqsize = n.getChannel("p", "q").getContent().size();
		int prsize = n.getChannel("p", "r").getContent().size();
		int pssize = n.getChannel("p", "s").getContent().size();

		// Token contains IDs p,q,r,s: p should finish
		receiveOrCatch(p, m, n.getChannel("q", "p"));
		assertTrue(p.isPassive());

		// p should not forward the token when finishing
		assertEquals(pqsize, n.getChannel("p", "q").getContent().size());
		assertEquals(prsize, n.getChannel("p", "r").getContent().size());
		assertEquals(pssize, n.getChannel("p", "s").getContent().size());
	}

	/**
	 * receiveTest11:
	 * A non-initiator should also finish normally
	 * (we don't check the return TOKEN here, for that
	 * see receiveTest9)
	 */
	@Test
	void receiveTest11() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("r");
		m.addId("s");

		assertFalse(q.isPassive());

		// Token contains IDs p,r,s: q should finish (and return token to parent, but that is checked in another test)
		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertTrue(q.isPassive());
	}

	/**
	 * simulationTest1:
	 * Simulate a full run of the algorithm on a network with
	 * one initiator and 15 non-initiators.
	 * All processes should be finished at the end.
	 */
	@Test
	void simulationTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraPiggybackInitiator");
		for (int i = 0; i < 15; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraPiggybackNonInitiator");
		}
		n.makeComplete();
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(n.simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		// No output, check internal state:
		// All processes should have finished
		assertTrue(((WaveProcess) n.getProcess("p")).isPassive());
		for (int i = 0; i < 15; i++) {
			assertTrue(((WaveProcess) n.getProcess("q" + i)).isPassive());
		}
	}
}
