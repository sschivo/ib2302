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

	/**
	 * initTest1:
	 * As the designated initiator, upon init() p should broadcast
	 * a control (marker) message to every outgoing channel.
	 * Verifies all other processes receive exactly one marker.
	 */
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

	/**
	 * initTest2:
	 * Non-initiator processes must not send any markers on init().
	 * Ensures only the initiator kick-starts the snapshot.
	 */
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
	}

	/**
	 * receiveTest1:
	 * Passing an unsupported Message (e.g. Message.DUMMY) should be rejected.
	 * Expects IllegalReceiveException on any invalid receive().
	 */
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
	/**
	 * receiveTest2:
	 * Receiving the same control marker twice from one neighbor is illegal.
	 * Expects IllegalReceiveException on the second marker from the same channel.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new ChandyLamportControlMessage(), n.getChannel("q", "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new ChandyLamportControlMessage(), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest3:
	 * Once a process has completed its snapshot, further control markers
	 * must be rejected. Verifies exception when receiving marker post-completion.
	 */
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

	/**
	 * receiveTest4:
	 * Before any marker arrives, basic application messages should not be recorded.
	 * Confirms that only after the first marker does state-capture begin.
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new ChandyLamportControlMessage(), n.getChannel("p", "q"));
		assertTrue(q.hasStarted());
	}

	/**
	 * receiveTest5:
	 * After receiving markers on all incoming channels,
	 * the process finalizes its snapshot and enters "finished" state.
	 * Verifies that no further markers are expected and channel states are frozen.
	 */
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
	/**
	 * receiveTest6:
	 * While snapshot not started, basic messages on channels must be ignored
	 * (not treated as in-transit). Ensures no channel element is recorded prematurely.
	 */
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p:week34.ChandyLamportInitiator q,r:week34.ChandyLamportNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());

		receiveOrCatch(q, new ChandyLamportBasicMessage("smurf"), n.getChannel("p", "q"));
		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());
	}

	/**
	 * receiveTest7:
	 * After snapshot completion, subsequent basic messages must not be recorded.
	 * Confirms that channel content remains unchanged post-snapshot.
	 */
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

	/**
	 * receiveTest8:
	 * Upon receiving a marker on a channel whose marker was already processed,
	 * subsequent basic messages on that channel should not be recorded.
	 * Verifies per-channel "once only" recording rule.
	 */
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
