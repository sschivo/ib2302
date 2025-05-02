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

	/**
	 * initTest1:
	 * The initiator should send control messages on its outgoing channels.
	 */
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

	/**
	 * initTest2:
	 * Non-initiators must not send any control message
	 * until they first receive one.
	 */
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
	}

	/**
	 * receiveTest1:
	 * Receiving an unsupported Message type must be rejected.
	 * Expects IllegalReceiveException on Message.DUMMY.
	 */
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
	/**
	 * receiveTest2:
	 * Receiving a second control message from the same neighbor is illegal.
	 * Expects IllegalReceiveException on duplicate message.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new LaiYangControlMessage(0), n.getChannel("q", "p")));
	}

	/**
	 * receiveTest3:
	 * After a process has completed its snapshot, any further control messages
	 * must be rejected. Verifies exception on post-completion message receives.
	 */
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

	/**
	 * receiveTest4:
	 * On first control message receipt, process should
	 * start the snapshot.
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new LaiYangControlMessage(0), n.getChannel("p", "q"));
		assertTrue(q.hasStarted());
	}

	/**
	 * receiveTest5:
	 * Without having received all control messages, the snapshot is not yet complete.
	 * Verifies process remains active and does not finalize.
	 */
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertFalse(p.hasFinished());

		receiveOrCatch(p, new LaiYangControlMessage(0), n.getChannel("q", "p"));
		assertFalse(p.hasFinished());
	}

	/**
	 * receiveTest6:
	 * If all control messages have been received, but not all corresponding basic messages,
	 * the snapshot cannot finish yet.
	 */
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

	/**
	 * receiveTest7:
	 * If all control messages and all corresponding basic messages have
	 * been received, the process should finalize the snapshot.
	 */
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

	/**
	 * receiveTest8:
	 * Same as receiveTest7, just a bigger example including basic messages
	 */
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

	/**
	 * receiveTest9:
	 * Same as receiveTest7 and 8, even more basic messages
	 */
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
	/**
	 * receiveTest10:
	 * If a message arrives with the "True" tag before snapshot start,
	 * the process should start the snapshot immediately.
	 */
	@Test
	void receiveTest10() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new LaiYangBasicMessage("chef smurf", true), n.getChannel("p", "q"));
		assertTrue(q.hasStarted());
	}

	/**
	 * receiveTest11:
	 * If a message arrives with the "False" tag before snapshot start,
	 * the process should *not* start the snapshot.
	 */
	@Test
	void receiveTest11() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertFalse(q.hasStarted());

		receiveOrCatch(q, new LaiYangBasicMessage("greedy smurf", false), n.getChannel("p", "q"));
		assertFalse(q.hasStarted());
	}

	/**
	 * receiveTest12:
	 * After snapshot completion, any further "False"-tagged messages
	 * must be rejected. Expects IllegalReceiveException.
	 */
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

	/**
	 * receiveTest13:
	 * If still waiting for control messages on some channels,
	 * receiving a "False"-tagged message should not complete snapshot.
	 */
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

	/**
	 * receiveTest14:
	 * If all control messages have been received, but not all basic messages,
	 * the process should *not* finish the snapshot upon receipt of a "False"-tagged message.
	 */
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

	/**
	 * receiveTest15:
	 * If all control messages have been received, and also all basic messages,
	 * the process should finish the snapshot upon receipt of a "False"-tagged message.
	 */
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

	/**
	 * receiveTest16:
	 * While the snapshot has not been started yet, basic messages should not be recorded.
	 * Confirms no channel-state logging before tag flip.
	 */
	@Test
	void receiveTest16() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess q = (SnapshotProcess) n.getProcess("q");
		q.init();

		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());


		receiveOrCatch(q, new LaiYangBasicMessage("poet smurf", true), n.getChannel("p", "q"));
		assertEquals(0, q.getChannelState(n.getChannel("p", "q")).size());
	}

	/**
	 * receiveTest17:
	 * If tag true and started, do not record
	 */
	@Test
	void receiveTest17() {
		Network n = Network.parse(true, "p:week34.LaiYangInitiator q,r:week34.LaiYangNonInitiator").makeComplete();

		SnapshotProcess p = (SnapshotProcess) n.getProcess("p");
		p.init();

		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());


		receiveOrCatch(p, new LaiYangBasicMessage("baby smurf", true), n.getChannel("q", "p"));
		assertEquals(0, p.getChannelState(n.getChannel("q", "p")).size());
	}

	/**
	 * receiveTest18:
	 * If tag false and not started, do not record
	 */
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
