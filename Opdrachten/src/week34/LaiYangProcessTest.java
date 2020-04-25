package week34;

import static framework.ProcessTests.receiveOrCatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Network;

class LaiYangProcessTest {
	
	// Check that initiator actually initiates.
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertTrue(p.hasStarted());

		Collection<Message> pq = n.getChannel("p", "q").getContent();
		Collection<Message> pr = n.getChannel("p", "r").getContent();
		assertEquals(1, pq.size());
		assertEquals(1, pr.size());
		assertTrue(pq.iterator().next() instanceof LaiYangControlMessage);
		assertTrue(pr.iterator().next() instanceof LaiYangControlMessage);
	}

	// Non-initiators should not have done anything.
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
	}

	// Illegal message type
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		p.init();
		q.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	// ===== Control messages =====
	// Double receive
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new LaiYangControlMessage(0), n.getChannel("q", "p")));
	}

	// Receive when finished
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new LaiYangControlMessage(0), n.getChannel("q", "p")));
	}

	// If not started, start
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new LaiYangControlMessage(0), n.getChannel("p", "q"));
		assertTrue(q.hasStarted());
	}

	// If not received all, do not finish
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());
	}

	// If received all but not all corresponding basic messages, do not finish
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(1), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());
	}

	// If received all and all corresponding basic messages, finish
	@Test
	void receiveTest7() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());
	}

	@Test
	void receiveTest8() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());
		
		receiveOrCatch(p, new LaiYangBasicMessage("papa smurf", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());
		
		receiveOrCatch(p, new LaiYangBasicMessage("smurfette", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(2), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());
	}

	@Test
	void receiveTest9() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangBasicMessage("jokey smurf", true), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());
		
		receiveOrCatch(p, new LaiYangBasicMessage("hefty smurf", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangBasicMessage("grouchy smurf", true), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());
		
		receiveOrCatch(p, new LaiYangBasicMessage("brainy smurf", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(2), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());
	}

	// ===== Basic messages =====
	// If tag true and not started, start
	@Test
	void receiveTest10() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new LaiYangBasicMessage("chef smurf", true), n.getChannel("p", "q"));
		assertTrue(q.hasStarted());
	}

	// If tag false and not started, do not start
	@Test
	void receiveTest11() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new LaiYangBasicMessage("greedy smurf", false), n.getChannel("p", "q"));
		assertFalse(q.hasStarted());
	}

	// If tag false and finished, throw
	@Test
	void receiveTest12() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new LaiYangBasicMessage("vanity smurf", false), n.getChannel("q", "p")));
	}

	// If tag false and did not receive all control messages, do not finish
	@Test
	void receiveTest13() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(1), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangBasicMessage("handy smurf", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());
	}

	// If tag false, did receive all control messages but not all corresponding basic messages, do not finish
	@Test
	void receiveTest14() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(1), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(1), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangBasicMessage("scaredy smurf", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());
	}

	// If tag false, did receive all control messages and all corresponding basic messages, finish
	@Test
	void receiveTest15() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangBasicMessage("lazy smurf", true), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());
		
		receiveOrCatch(p, new LaiYangBasicMessage("farmer smurf", false), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangBasicMessage("harmony smurf", true), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(2), n.getChannel("r", "p"));
		assertFalse(p.hasFinished());
		
		receiveOrCatch(p, new LaiYangBasicMessage("painter smurf", false), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());
	}

	// If tag true and not started, do not record
	@Test
	void receiveTest16() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());


		receiveOrCatch(q, new LaiYangBasicMessage("poet smurf", true), n.getChannel("p", "q"));
		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());
	}

	// If tag true and started, do not record
	@Test
	void receiveTest17() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());


		receiveOrCatch(p, new LaiYangBasicMessage("baby smurf", true), n.getChannel("q", "p"));
		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());
	}

	// If tag false and not started, do not record
	@Test
	void receiveTest18() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());


		receiveOrCatch(q, new LaiYangBasicMessage("baker smurf", false), n.getChannel("p", "q"));
		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());
	}
}
