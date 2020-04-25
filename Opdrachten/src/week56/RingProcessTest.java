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

	// Initiator should initiate
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

	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());

		Collection<Message> qr = n.getChannel("q", "r").getContent();
		assertEquals(0, qr.size());
	}

	// Initiator illegal message: throw
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("r", "p")));
	}

	// Initiator double receive: throw
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();
		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));

		assertThrows(IllegalReceiveException.class, () -> p.receive(new TokenMessage(), n.getChannel("r", "p")));
	}

	// Initiator legal receive: finish
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));

		assertTrue(p.isPassive());
	}

	// Non-initiator illegal message: throw
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	// Non-initiator double receive: throw
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week56.RingInitiator q,r:week56.RingNonInitiator p->q q->r r->p");

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();
		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		assertThrows(IllegalReceiveException.class, () -> q.receive(new TokenMessage(), n.getChannel("p", "q")));
	}

	// Non-initiator legal receive: start and finish
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

		// No output, check internal state
		assertTrue(((WaveProcess) n.getProcess("p")).isPassive());
		for (int i = 0; i < 20; i++) {
			assertTrue(((WaveProcess) n.getProcess("q" + i)).isPassive());
		}
	}
}
