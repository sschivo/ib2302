package week56;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Network;

class RingProcessTest {

	/**
	 * initTest1:
	 * The initiator does not immediately terminate,
	 * and sends exactly one TOKEN to its clockwise neighbor.
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertTrue(p.isActive());
		assertFalse(p.isPassive());

		Collection<Message> pq = n.getChannel("p", "q").getContent();
		assertEquals(1, pq.size());
		assertTrue(pq.iterator().next() instanceof TokenMessage);
	}

	/**
	 * initTest2:
	 * Any non-initiator does not immediately terminate,
	 * and cannot (yet) send any TOKEN.
	 */
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());

		Collection<Message> qr = n.getChannel("q", "r").getContent();
		assertEquals(0, qr.size());
	}

	/**
	 * receiveTest1:
	 * Invalid message type causes IllegalReceiveException.
	 * (Initiator)
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("r", "p")));
	}

	/**
	 * receiveTest2:
	 * Duplicate TOKEN from the same neighbor should be rejected.
	 * (Initiator)
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();
		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));

		assertThrows(IllegalReceiveException.class, () -> p.receive(new TokenMessage(), n.getChannel("r", "p")));
	}

	/**
	 * receiveTest3:
	 * Initiator receives the TOKEN: finish.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));

		assertTrue(p.isPassive());
	}

	/**
	 * receiveTest4:
	 * Invalid message type causes IllegalReceiveException.
	 * (Non-initiator)
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	/**
	 * receiveTest5:
	 * Duplicate TOKEN from the same neighbor should be rejected.
	 * (Non-initiator)
	 */
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();
		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		assertThrows(IllegalReceiveException.class, () -> q.receive(new TokenMessage(), n.getChannel("p", "q")));
	}

	/**
	 * receiveTest6:
	 * On valid receive, non‐initiator forwards the TOKEN and then finishes.
	 */
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());
		assertFalse(q.isPassive());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		assertFalse(q.isActive());
		assertTrue(q.isPassive());
	}

	/**
	 * simulationTest1:
	 * Do a full simulation of the algorithm.
	 * All processes should be finished at the end.
	 */
	@Test
	void simulationTest1() {
		Network n = Network.parse(true, "p:week56.RingInitiator");
		for (int i = 0; i < 20; i++) {
			n.addProcess("q" + i, "week56.RingNonInitiator");
		}
		n.addRing();
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(n.simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		// No output, check internal state:
		// All processes should have finished
		assertTrue(((WaveProcess) n.getProcess("p")).isPassive());
		for (int i = 0; i < 20; i++) {
			assertTrue(((WaveProcess) n.getProcess("q" + i)).isPassive());
		}
	}
}
