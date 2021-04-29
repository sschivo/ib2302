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

	// Initiator should initiate
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

	// Initiator illegal message: throw
	@Test
	void receiveTest1() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	// Non-initiator illegal message: throw
	@Test
	void receiveTest2() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	// Initiator receive when finished: throw
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

	// Non-initiator receive when finished: throw
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

	// Non-initiator starts on first receive
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

	// Non-initiator piggybacks its ID
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

	// Initiator forwards to unvisited
	@Test
	void receiveTest7() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		// p sends to random process, so these are the new reference values.
		int pqsize = n.getChannel("p", "q").getContent().size();
		int prsize = n.getChannel("p", "r").getContent().size();
		int pssize = n.getChannel("p", "s").getContent().size();

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

	// Non-initiator forwards to unvisited (non-parent)
	@Test
	void receiveTest8() {
		Network n = Network.parse(true,
				"p:week56.DepthFirstSearchExtraPiggybackInitiator q,r,s:week56.DepthFirstSearchExtraPiggybackNonInitiator")
				.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		TokenWithIdsMessage m = new TokenWithIdsMessage("p");
		m.addId("r");

		// Parent is set to p, so the below tests should be fine.
		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
		assertEquals(1, n.getChannel("q", "s").getContent().size());
	}

	// Non-initiator returns to parent
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

		// Sets p as the parent
		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertEquals(1, qp.getContent().size());
		assertTrue(qp.getContent().iterator().next() instanceof TokenWithIdsMessage);
	}

	// Initiator finishes
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

		receiveOrCatch(p, m, n.getChannel("q", "p"));
		assertTrue(p.isPassive());

		assertEquals(pqsize, n.getChannel("p", "q").getContent().size());
		assertEquals(prsize, n.getChannel("p", "r").getContent().size());
		assertEquals(pssize, n.getChannel("p", "s").getContent().size());
	}

	// Non-initiator finishes
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

		receiveOrCatch(q, m, n.getChannel("p", "q"));
		assertTrue(q.isPassive());
	}

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

		// No output, check internal state
		assertTrue(((WaveProcess) n.getProcess("p")).isPassive());
		for (int i = 0; i < 15; i++) {
			assertTrue(((WaveProcess) n.getProcess("q" + i)).isPassive());
		}
	}
}
