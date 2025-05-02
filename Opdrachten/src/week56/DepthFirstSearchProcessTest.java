package week56;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Channel;
import framework.Network;

class DepthFirstSearchProcessTest {

	/**
	 * initTest1:
	 * Initiator should not finish and should send a single token on init
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertTrue(p.isActive());
		assertFalse(p.isPassive());

		int sum = 0;
		Collection<Message> pout;
		for (Channel d : p.getOutgoing()) {
			pout = d.getContent();
			if (pout.size() > 0) {
				assertTrue(pout.iterator().next() instanceof TokenMessage);
				sum += pout.size();
			}
		}
		assertEquals(1, sum);
	}

	/**
	 * initTest2:
	 * Non-initiators should not finish and should not send anything on init
	 */
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());
		assertFalse(q.isPassive());

		int sum = 0;
		for (Channel d : q.getOutgoing()) {
			sum += d.getContent().size();
		}
		assertEquals(0, sum);
	}

	/**
	 * receiveTest1:
	 * Initiator received an illegal message type: throw exception
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("r", "p")));
	}

	/**
	 * receiveTest2:
	 * Initiator receives a TOKEN, but there are other neighbours
	 * that can receive it according to the rules: forward the
	 * TOKEN to another neighbour.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		int sum = 0;
		Collection<Message> pout;
		for (Channel d : p.getOutgoing()) {
			pout = d.getContent();
			if (pout.size() > 0) {
				assertTrue(pout.iterator().next() instanceof TokenMessage);
				sum += pout.size();
			}
		}
		assertEquals(1, sum); //p has sent exactly one TOKEN message

		receiveOrCatch(p, new TokenMessage(), n.getChannel("q", "p")); //q replied with a TOKEN

		sum = 0;
		for (Channel d : p.getOutgoing()) {
			pout = d.getContent();
			if (pout.size() > 0) {
				assertEquals(1, pout.size());
				assertTrue(pout.iterator().next() instanceof TokenMessage);
				sum += pout.size();
			}
		}
		assertEquals(2, sum); //Now p has sent the TOKEN to two distinct neighbours
	}

	/**
	 * receiveTest3:
	 * Initiator receives a TOKEN, but there are still
	 * neighbours who have not yet sent their TOKEN:
	 * the initiator cannot finish now.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("q", "p"));
		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));
		assertFalse(p.isPassive()); //p must still be active (i.e. not finished): it has not heard from s yet
	}

	/**
	 * receiveTest4:
	 * The initiator does not send TOKEN through the same channel twice
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		int sum = 0;
		for (Channel d : p.getOutgoing()) {
			sum += d.getContent().size();
		}
		assertEquals(1, sum);

		for (int i = 1; i < 100; i++) {
			receiveOrCatch(p, new TokenMessage(), n.getChannel("q" + i, "p"));

			sum = 0;
			for (Channel d : p.getOutgoing()) {
				sum += d.getContent().size();
			}
			assertEquals(i + 1, sum);
		}

		Collection<Message> pout;
		for (Channel d : p.getOutgoing()) {
			pout = d.getContent();
			if (pout.size() > 0) {
				assertEquals(1, pout.size());
				assertTrue(pout.iterator().next() instanceof TokenMessage);
			}
		}
	}

	/**
	 * receiveTest5:
	 * Initiator has received a TOKEN from each of the neighbours:
	 * now it can finish.
	 */
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("q", "p"));
		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));
		assertFalse(p.isPassive());

		receiveOrCatch(p, new TokenMessage(), n.getChannel("s", "p"));
		assertTrue(p.isPassive());
	}

	/**
	 * receiveTest6:
	 * Initiator receives a TOKEN when it has already finished: throw exception
	 */
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new TokenMessage(), n.getChannel("q", "p"));
		receiveOrCatch(p, new TokenMessage(), n.getChannel("r", "p"));
		receiveOrCatch(p, new TokenMessage(), n.getChannel("s", "p"));
		assertTrue(p.isPassive());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new TokenMessage(), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest7:
	 * Non-initiator received an illegal message type: throw exception
	 */
	@Test
	void receiveTest7() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("r", "q")));
	}

	/**
	 * receiveTest8:
	 * Non-initiator receives a TOKEN, but not all neighbours have
	 * sent one: forward TOKEN to another neighbour
	 */
	@Test
	void receiveTest8() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		int sum = 0;
		for (Channel d : q.getOutgoing()) {
			sum += d.getContent().size();
		}
		assertEquals(0, sum);

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		sum = 0;
		for (Channel d : q.getOutgoing()) {
			sum += d.getContent().size();
		}
		assertEquals(1, sum);
		assertTrue(q.isActive());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("r", "q"));

		sum = 0;
		for (Channel d : q.getOutgoing()) {
			sum += d.getContent().size();
		}
		assertEquals(2, sum);

		Collection<Message> pout;
		for (Channel d : q.getOutgoing()) {
			pout = d.getContent();
			if (pout.size() > 0) {
				assertEquals(1, pout.size());
				assertTrue(pout.iterator().next() instanceof TokenMessage);
			}
		}
	}

	/**
	 * receiveTest9:
	 * Non-initiator receives a TOKEN, but there are still
	 * neighbours who have not yet sent their TOKEN:
	 * the initiator cannot finish now.
	 */
	@Test
	void receiveTest9() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());
		assertFalse(q.isPassive());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));
		assertFalse(q.isPassive());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("r", "q"));
		assertFalse(q.isPassive());
	}

	/**
	 * receiveTest10:
	 * A non-initiator does not send TOKEN through the same channel twice
	 */
	@Test
	void receiveTest10() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
		}
		n.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q0");
		q.init();

		int sum = 0;
		for (Channel d : q.getOutgoing()) {
			sum += d.getContent().size();
		}
		assertEquals(0, sum);

		Collection<Message> pout;
		for (int i = 1; i < 100; i++) {
			receiveOrCatch(q, new TokenMessage(), n.getChannel("q" + i, "q0"));

			sum = 0;
			for (Channel d : q.getOutgoing()) {
				pout = d.getContent();
				if (pout.size() > 0) {
					assertEquals(1, pout.size());
					assertTrue(pout.iterator().next() instanceof TokenMessage);
					sum += pout.size();
				}
			}
			assertEquals(i, sum);
		}
	}

	/**
	 * receiveTest11:
	 * A non-initiator can forward TOKEN to its parent
	 * only if it is the last option left
	 */
	@Test
	void receiveTest11() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
		}
		n.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q0");
		q.init();

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q0"));

		for (int i = 2; i < 100; i++) {
			receiveOrCatch(q, new TokenMessage(), n.getChannel("q" + i, "q0"));
		}
		assertEquals(0, n.getChannel("q0", "p").getContent().size());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("q1", "q0"));
		assertEquals(1, n.getChannel("q0", "p").getContent().size());
	}

	/**
	 * receiveTest12:
	 * A non-initiator receives the last TOKEN from a neighbour:
	 * now it can finish
	 */
	@Test
	void receiveTest12() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
		}
		n.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q0"); //q0 is one of the non-initiators
		q.init();

		assertFalse(q.isPassive());

		for (int i = 1; i < 100; i++) { //q0 receves TOKEN from each of the neighbours
			receiveOrCatch(q, new TokenMessage(), n.getChannel("q" + i, "q0"));
			assertFalse(q.isPassive()); //after each of those TOKEN messages, q0 is still active
		}

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q0")); //q0 receives TOKEN from the last of the neighbours (coincidentally it's from the initiator, but it doesn't matter here)
		assertTrue(q.isPassive()); //now q0 should have finished
	}

	/**
	 * receiveTest13:
	 * A non-initiator receives a TOKEN when it has already finished: throw exception
	 */
	@Test
	void receiveTest13() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator q,r,s:week56.DepthFirstSearchNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));
		receiveOrCatch(q, new TokenMessage(), n.getChannel("r", "q"));
		receiveOrCatch(q, new TokenMessage(), n.getChannel("s", "q"));
		assertTrue(q.isPassive());

		assertThrows(IllegalReceiveException.class, () -> q.receive(new TokenMessage(), n.getChannel("r", "q")));
	}

	/**
	 * receiveTest14:
	 * Initiator receives TOKEN: if allowed by the rules, return to sender (DFS rule)
	 */
	@Test
	void receiveTest14() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		for (int i = 0; i < 99; i++) {
			int j = (2 * i) % 99;

			receiveOrCatch(p, new TokenMessage(), n.getChannel("q" + j, "p"));

			assertEquals(1, n.getChannel("p", "q" + j).getContent().size());
		}
	}

	/**
	 * receiveTest15:
	 * Non-initiator receives TOKEN: if allowed by the rules, return to sender (DFS rule)
	 */
	@Test
	void receiveTest15() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
		}
		n.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q0");
		q.init();

		// Set parent
		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q0"));

		for (int i = 0; i < 99; i++) {
			int j = (2 * i) % 99 + 1;

			receiveOrCatch(q, new TokenMessage(), n.getChannel("q" + j, "q0"));

			assertEquals(1, n.getChannel("q0", "q" + j).getContent().size());
		}
	}

	/**
	 * simulationTest1:
	 * Simulate a full run of the algorithm.
	 * At the end each process should have finished.
	 */
	@Test
	void simulationTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchInitiator");
		for (int i = 0; i < 15; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchNonInitiator");
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
