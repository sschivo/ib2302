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

class ChandyLamportProcessTest {
	
	// Initiated initiator should send messages
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertTrue(p.hasStarted());

		Collection<Message> pq = n.getChannel("p", "q").getContent();
		Collection<Message> pr = n.getChannel("p", "r").getContent();
		assertEquals(1, pq.size());
		assertEquals(1, pr.size());
		assertTrue(pq.iterator().next() instanceof ChandyLamportControlMessage);
		assertTrue(pr.iterator().next() instanceof ChandyLamportControlMessage);
	}

	// Initiated non-initiators should not send messages
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
	}

	// Throw exception on illegal message type
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		p.init();
		q.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	// ===== Control messages =====
	// Throw exception on double receive of control message
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("q", "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new ChandyLamportControlMessage(), n.getChannel("q", "p")));
	}

	// Throw exception on receive of control message when finished
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("q", "p"));
		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new ChandyLamportControlMessage(), n.getChannel("q", "p")));
	}

	// If not started, start on control message
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new ChandyLamportControlMessage(), n.getChannel("p", "q"));
		assertTrue(q.hasStarted());
	}

	// If received all control messages, finish
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());

		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("r", "p"));
		assertTrue(p.hasFinished());
	}

	// ===== Basic messages =====
	// If not started, do not record basic message
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());

		receiveOrCatch(q, new ChandyLamportBasicMessage("smurf"), n.getChannel("p", "q"));
		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());
	}

	// If finished, do not record basic message
	@Test
	void receiveTest7() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());

		p.finishSnapshot();
		receiveOrCatch(p, new ChandyLamportBasicMessage("smurf"), n.getChannel("q", "p"));
		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());
	}

	// If received corresponding control message, do not record basic message
	@Test
	void receiveTest8() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());

		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("q", "p"));
		receiveOrCatch(p, new ChandyLamportBasicMessage("smurf"), n.getChannel("q", "p"));
		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());
	}
}
