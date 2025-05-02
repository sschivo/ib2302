package week78;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Network;
import framework.Channel;

class BrachaTouegProcessTest {
	void addRequest(Network n, String from, String to) {
		BrachaTouegProcess p = (BrachaTouegProcess) n.getProcess(from);
		BrachaTouegProcess q = (BrachaTouegProcess) n.getProcess(to);
		Channel pq = n.getChannel(from, to);

		p.addOutRequest(pq);
		q.addInRequest(pq);
	}

	/**
	 * Test network 1 is based on Example 5.3 (see book and slides).
	 * Requests:
	 *  u->w and x
	 *  v->u and x
	 *  w->v and x
	 * Initiator: u
	 * This network is deadlocked.
	 */
	Network network1() {		
		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("v")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("w")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);

		addRequest(n, "u", "w");
		addRequest(n, "u", "x");
		addRequest(n, "v", "u");
		addRequest(n, "v", "x");
		addRequest(n, "w", "v");
		addRequest(n, "w", "x");

		return n;
	}

	/**
	 * Test network 2 is based on Example 5.4 (see book and slides)
	 * Requests:
	 *  u->w and x
	 *  v->u and x
	 *  w->v or x
	 * Initiator: u
	 * This network is not deadlocked.
	 */
	Network network2() {
		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("v")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);

		addRequest(n, "u", "w");
		addRequest(n, "u", "x");
		addRequest(n, "v", "u");
		addRequest(n, "v", "x");
		addRequest(n, "w", "v");
		addRequest(n, "w", "x");

		return n;
	}

	/**
	 * Test network 3 is based on Example 5.5 (see book and slides)
	 * Requests:
	 *  u->v and x
	 *  v->w
	 *  w->x
	 * Initiator: u
	 * This network is not deadlocked.
	 */
	Network network3() {
		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("v")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);

		addRequest(n, "u", "v");
		addRequest(n, "u", "x");
		addRequest(n, "v", "w");
		addRequest(n, "w", "x");

		return n;
	}

	/**
	 * Test network 4 is custom-made
	 * Requests:
	 *  v->u
	 *  w->u
	 *  x->v and w
	 * Initiator: u
	 * This network is not deadlocked.
	 */
	Network network4() {
		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
		((BrachaTouegProcess) n.getProcess("u")).setRequests(0);
		((BrachaTouegProcess) n.getProcess("v")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("x")).setRequests(2);

		addRequest(n, "v", "u");
		addRequest(n, "w", "u");
		addRequest(n, "x", "v");
		addRequest(n, "x", "w");

		return n;
	}

	/**
	 * Test network 5 is based on exercise VIII-1
	 * from the exercises on Chapter 5.
	 * Requests:
	 *  u->v and x
	 *  v->w
	 *  w->x
	 * Initiator: u (here we use only the first solution, where u is chosen as initiator)
	 */
	Network network5() {
		Network n = Network.parse(true, "u:week78.BrachaTouegInitiator v,w,x:week78.BrachaTouegNonInitiator").makeComplete();
		((BrachaTouegProcess) n.getProcess("u")).setRequests(2);
		((BrachaTouegProcess) n.getProcess("v")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("w")).setRequests(1);
		((BrachaTouegProcess) n.getProcess("x")).setRequests(0);

		addRequest(n, "u", "v");
		addRequest(n, "u", "x");
		addRequest(n, "v", "w");
		addRequest(n, "w", "x");

		return n;
	}

	/**
	 * initTest1:
	 * The initiator should send NOTIFY to all nodes
	 * in its Out-list (list is non-empty).
	 */
	@Test
	void initTest1() {
		Network n = network1();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertEquals(0, n.getChannel("u", "v").getContent().size());
		assertEquals(1, n.getChannel("u", "w").getContent().size());
		assertEquals(1, n.getChannel("u", "x").getContent().size());
		assertTrue(n.getChannel("u", "w").getContent().iterator().next() instanceof NotifyMessage);
		assertTrue(n.getChannel("u", "x").getContent().iterator().next() instanceof NotifyMessage);
	}

	/**
	 * initTest2:
	 * The initiator has an empty Out-list: should
	 * directly start sending GRANTs.
	 */
	@Test
	void initTest2() {
		Network n = network4();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertEquals(1, n.getChannel("u", "v").getContent().size());
		assertEquals(1, n.getChannel("u", "w").getContent().size());
		assertEquals(0, n.getChannel("u", "x").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().iterator().next() instanceof GrantMessage);
		assertTrue(n.getChannel("u", "w").getContent().iterator().next() instanceof GrantMessage);
	}

	/**
	 * initTest3:
	 * A non-initiator does nothing upon init.
	 */
	@Test
	void initTest3() {
		Network n = network1();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		assertEquals(0, n.getChannel("v", "u").getContent().size());
		assertEquals(0, n.getChannel("v", "w").getContent().size());
		assertEquals(0, n.getChannel("v", "x").getContent().size());
	}



	/**
	 * Message handling and exceptions.
	 * By now it should be clear what the expectations are,
	 * these are checked in the following tests.
	 */

	// Illegal message type
	// Initiator
	@Test
	void receiveTest1() {
		Network n = network3();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertThrows(IllegalReceiveException.class, () -> u.receive(Message.DUMMY, n.getChannel("v", "u")));
	}
	// Non-initiator
	@Test
	void receiveTest2() {
		Network n = network3();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		assertThrows(IllegalReceiveException.class, () -> v.receive(Message.DUMMY, n.getChannel("u", "v")));
	}

	// Unexpected DONE: should only receive DONE from neighbours where NOTIFY was sent
	// Initiator
	@Test
	void receiveTest3() {
		Network n = network3();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		// u did not notify w
		assertThrows(IllegalReceiveException.class, () -> u.receive(new DoneMessage(), n.getChannel("w", "u")));
	}
	// Non-initiator
	@Test
	void receiveTest4() {
		Network n = network3();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("u", "v"));

		// v did not notify x
		assertThrows(IllegalReceiveException.class, () -> v.receive(new DoneMessage(), n.getChannel("x", "v")));
	}

	// Double DONE: should only receive a single one from every NOTIFYed neighbour
	// Initiator
	@Test
	void receiveTest5() {
		Network n = network3();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new DoneMessage(), n.getChannel("v", "u"));
		assertThrows(IllegalReceiveException.class, () -> u.receive(new DoneMessage(), n.getChannel("v", "u")));
	}
	// Non-initiator
	@Test
	void receiveTest6() {
		Network n = network3();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("u", "v"));

		receiveOrCatch(v, new DoneMessage(), n.getChannel("w", "v"));
		assertThrows(IllegalReceiveException.class, () -> v.receive(new DoneMessage(), n.getChannel("w", "v")));
	}

	// Unexpected ACK: should only receive ACK from neighbours where GRANT was sent
	// Initiator
	@Test
	void receiveTest7() {
		Network n = network4();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		// u did not grant to x
		assertThrows(IllegalReceiveException.class, () -> u.receive(new AckMessage(), n.getChannel("x", "u")));
	}
	// Non-initiator
	@Test
	void receiveTest8() {
		Network n = network3();

		BrachaTouegProcess x = (BrachaTouegProcess) n.getProcess("x");
		x.init();

		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));

		// x did not grant to v
		assertThrows(IllegalReceiveException.class, () -> x.receive(new AckMessage(), n.getChannel("v", "x")));
	}

	// Double ACK: should only receive a single ACK from every GRANTed neighbour
	// Initiator
	@Test
	void receiveTest9() {
		Network n = network4();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new AckMessage(), n.getChannel("v", "u"));
		assertThrows(IllegalReceiveException.class, () -> u.receive(new AckMessage(), n.getChannel("v", "u")));
	}
	// Non-initiator
	@Test
	void receiveTest10() {
		Network n = network3();

		BrachaTouegProcess x = (BrachaTouegProcess) n.getProcess("x");
		x.init();

		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));

		receiveOrCatch(x, new AckMessage(), n.getChannel("u", "x"));
		assertThrows(IllegalReceiveException.class, () -> x.receive(new AckMessage(), n.getChannel("u", "x")));
	}


	/**
	 * Handling normal messages and decisions about deadlock.
	 * The first group of tests are all for the initiator,
	 * the second group for the non-initiators.
	 */

	// ===== Initiator =====
	/**
	 * receiveTest11:
	 * If the initiator receives NOTIFY, it should return DONE
	 * because the initiator is already busy with the algorithm.
	 */
	@Test
	void receiveTest11() {
		Network n = network2();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertEquals(0, n.getChannel("u", "v").getContent().size());

		// Not the first notify for u (u notified itself): should return done to v
		receiveOrCatch(u, new NotifyMessage(), n.getChannel("v", "u"));

		assertEquals(1, n.getChannel("u", "v").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().iterator().next() instanceof DoneMessage);
	}

	/**
	 * receiveTest12:
	 * The algorithm terminates: the initiator
	 * should correctly decide that there is a deadlock.
	 * Note that Test network 2 itself is not deadlocked,
	 * we just "fake" reactions from other processes that would not
	 * occour if the algorithm were run normally with correct
	 * implementations for the non-initiators.
	 */
	@Test
	void receiveTest12() {
		Network n = network2();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		// x does not grant in this scenario and simply returns done
		receiveOrCatch(u, new DoneMessage(), n.getChannel("x", "u"));
		// w grants
		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		assertEquals(0, getPrinted(u).size());

		// w sends done. u still needs a grant from x, so u should consider itself deadlocked
		receiveOrCatch(u, new DoneMessage(), n.getChannel("w", "u"));
		assertEquals(1, getPrinted(u).size());
		assertEquals("false", getPrinted(u).iterator().next());
	}

	/**
	 * receiveTest13:
	 * The algorithm terminates: the initiator
	 * should correctly decide that there is no deadlock.
	 */
	@Test
	void receiveTest13() {
		Network n = network2();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		// u receives grants from x,w, a done from w and an ack from v
		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		receiveOrCatch(u, new DoneMessage(), n.getChannel("x", "u"));
		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		receiveOrCatch(u, new AckMessage(), n.getChannel("v", "u"));
		assertEquals(0, getPrinted(u).size());

		// u receives the final done from w, should consider itself free
		receiveOrCatch(u, new DoneMessage(), n.getChannel("w", "u"));
		assertEquals(1, getPrinted(u).size());
		assertEquals("true", getPrinted(u).iterator().next());
	}

	/**
	 * receiveTest14:
	 * The initiator should correctly manage GRANT messages
	 * according to the rules of the algorithm.
	 */
	@Test
	void receiveTest14() {
		Network n = network1();
		// Additional request from u to v
		addRequest(n, "u", "v");

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertEquals(1, n.getChannel("u", "v").getContent().size());
		assertEquals(1, n.getChannel("u", "w").getContent().size());
		assertEquals(1, n.getChannel("u", "x").getContent().size());

		// u receives grant from x: should return ack but not grant to v (requests is now 1)
		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		assertEquals(2, n.getChannel("u", "x").getContent().size());
		assertTrue(n.getChannel("u", "x").getContent().toArray()[1] instanceof AckMessage);
		assertEquals(1, n.getChannel("u", "v").getContent().size());

		// u receives grant from w: should grant to v (requests is now 0) but not yet return ack to w (wait for ack from v first)
		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());
		assertEquals(2, n.getChannel("u", "v").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().toArray()[1] instanceof GrantMessage);

		// u receives grant from v: should return ack
		receiveOrCatch(u, new GrantMessage(), n.getChannel("v", "u"));
		assertEquals(3, n.getChannel("u", "v").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().toArray()[2] instanceof AckMessage);
	}

	/**
	 * receiveTest15:
	 * The initiator should correctly manage ACK messages
	 * according to the rules of the algorithm.
	 * Note that we use a modified version of test network 1.
	 */
	@Test
	void receiveTest15() {
		Network n = network1();
		// Additional request from x to u
		addRequest(n, "x", "u");

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		// u receives grant from x: should return ack to x (not checked here)
		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());

		// u receives grant from w: should grant v,x (not checked here) and wait for acks
		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());

		// u receives ack but from x: should not return ack to w, still waiting for ack from v
		receiveOrCatch(u, new AckMessage(), n.getChannel("x", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());

		// u receives ack from v: should return ack to w
		receiveOrCatch(u, new AckMessage(), n.getChannel("v", "u"));
		assertEquals(2, n.getChannel("u", "w").getContent().size());
		assertTrue(n.getChannel("u", "w").getContent().toArray()[1] instanceof AckMessage);

		// Should have sent three messages to x (notify, ack, grant)
		assertEquals(3, n.getChannel("u", "x").getContent().size());
		// Should have sent one message to v (grant)
		assertEquals(1, n.getChannel("u", "v").getContent().size());
	}

	// ===== Non-initiator =====
	/**
	 * receiveTest16:
	 * If a non-initiator receives NOTIFY, it should react
	 * according to the rules of the algorithm.
	 */
	@Test
	void receiveTest16() {
		Network n = network1();

		// Initialise w,x
		BrachaTouegProcess w = (BrachaTouegProcess) n.getProcess("w");
		w.init();

		BrachaTouegProcess x = (BrachaTouegProcess) n.getProcess("x");
		x.init();

		assertEquals(0, n.getChannel("w", "u").getContent().size());
		assertEquals(0, n.getChannel("w", "v").getContent().size());
		assertEquals(0, n.getChannel("w", "x").getContent().size());

		assertEquals(0, n.getChannel("x", "u").getContent().size());
		assertEquals(0, n.getChannel("x", "v").getContent().size());
		assertEquals(0, n.getChannel("x", "w").getContent().size());

		// w receives notify from u: should notify v,x
		receiveOrCatch(w, new NotifyMessage(), n.getChannel("u", "w"));
		assertEquals(0, n.getChannel("w", "u").getContent().size());
		assertEquals(1, n.getChannel("w", "v").getContent().size());
		assertTrue(n.getChannel("w", "v").getContent().iterator().next() instanceof NotifyMessage);
		assertEquals(1, n.getChannel("w", "x").getContent().size());
		assertTrue(n.getChannel("w", "x").getContent().iterator().next() instanceof NotifyMessage);

		// x receives notify from w: should grant u,v,w
		receiveOrCatch(x, new NotifyMessage(), n.getChannel("w", "x"));
		assertEquals(1, n.getChannel("x", "u").getContent().size());
		assertTrue(n.getChannel("x", "u").getContent().iterator().next() instanceof GrantMessage);
		assertEquals(1, n.getChannel("x", "v").getContent().size());
		assertTrue(n.getChannel("x", "v").getContent().iterator().next() instanceof GrantMessage);
		assertEquals(1, n.getChannel("x", "w").getContent().size());
		assertTrue(n.getChannel("x", "w").getContent().iterator().next() instanceof GrantMessage);

		// x receives notify from u: should return done (not first notify)
		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));
		assertEquals(2, n.getChannel("x", "u").getContent().size());
		assertTrue(n.getChannel("x", "u").getContent().toArray()[1] instanceof DoneMessage);
		assertEquals(1, n.getChannel("x", "v").getContent().size());
		assertEquals(1, n.getChannel("x", "w").getContent().size());
	}

	/**
	 * receiveTest17:
	 * If a non-initiator receives DONE, it should react
	 * according to the rules of the algorithm.
	 */
	@Test
	void receiveTest17() {
		Network n = network2();

		// Initialise v,w
		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		BrachaTouegProcess w = (BrachaTouegProcess) n.getProcess("w");
		w.init();

		// w receives notify from u: should notify v,w
		receiveOrCatch(w, new NotifyMessage(), n.getChannel("u", "w"));
		// v receives notify from w: should notify u,x
		receiveOrCatch(v, new NotifyMessage(), n.getChannel("w", "v"));

		// v receives done from u: still waiting for x
		receiveOrCatch(v, new DoneMessage(), n.getChannel("u", "v"));
		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(0, n.getChannel("v", "w").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		// v receives done from x: should return done to w
		receiveOrCatch(v, new DoneMessage(), n.getChannel("x", "v"));
		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "w").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());
		assertTrue(n.getChannel("v", "w").getContent().toArray()[0] instanceof DoneMessage);

		// w receives done from x: should not do anything
		receiveOrCatch(w, new DoneMessage(), n.getChannel("x", "w"));
		assertEquals(0, n.getChannel("w", "u").getContent().size());
		assertEquals(1, n.getChannel("w", "v").getContent().size());
		assertEquals(1, n.getChannel("w", "x").getContent().size());

		// w receives done from v: should return done to u
		receiveOrCatch(w, new DoneMessage(), n.getChannel("v", "w"));
		assertEquals(1, n.getChannel("w", "u").getContent().size());
		assertEquals(1, n.getChannel("w", "v").getContent().size());
		assertEquals(1, n.getChannel("w", "x").getContent().size());
		assertTrue(n.getChannel("w", "u").getContent().toArray()[0] instanceof DoneMessage);
	}

	/**
	 * receiveTest18:
	 * If a non-initiator receives GRANT, it should react
	 * according to the rules of the algorithm.
	 */
	@Test
	void receiveTest18() {
		Network n = network1();
		// Additional request from v to w
		addRequest(n, "v", "w");

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		// v receives notify from w: should notify u,w,x
		receiveOrCatch(v, new NotifyMessage(), n.getChannel("w", "v"));

		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "w").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		// v receives grant from u: should ack u
		receiveOrCatch(v, new GrantMessage(), n.getChannel("u", "v"));
		assertEquals(2, n.getChannel("v", "u").getContent().size());
		assertTrue(n.getChannel("v", "u").getContent().toArray()[1] instanceof AckMessage);

		// v receives grant from x: should grant w and not ack x yet (wait for ack from w)
		receiveOrCatch(v, new GrantMessage(), n.getChannel("x", "v"));
		assertEquals(1, n.getChannel("v", "x").getContent().size());
		assertEquals(2, n.getChannel("v", "w").getContent().size());
		assertTrue(n.getChannel("v", "w").getContent().toArray()[1] instanceof GrantMessage);

		// v receives grant from w: should ack w
		receiveOrCatch(v, new GrantMessage(), n.getChannel("w", "v"));
		assertEquals(3, n.getChannel("v", "w").getContent().size());
		assertTrue(n.getChannel("v", "w").getContent().toArray()[2] instanceof AckMessage);
	}

	/**
	 * receiveTest19:
	 * If a non-initiator receives ACK, it should react
	 * according to the rules of the algorithm.
	 */
	@Test
	void receiveTest19() {
		Network n = network1();
		// Additional request from u to v
		addRequest(n, "u", "v");

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		// v receives notify from w: should notify u,x
		receiveOrCatch(v, new NotifyMessage(), n.getChannel("w", "v"));
		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		// v receives grant from u: should ack u
		receiveOrCatch(v, new GrantMessage(), n.getChannel("u", "v"));
		assertEquals(2, n.getChannel("v", "u").getContent().size());

		// v receives grant from x: should grant u,w
		receiveOrCatch(v, new GrantMessage(), n.getChannel("x", "v"));
		assertEquals(3, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "w").getContent().size());

		// v receives ack from w: should not ack x yet (waiting for ack from u)
		receiveOrCatch(v, new AckMessage(), n.getChannel("w", "v"));
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		// v receives ack from u: should ack x
		receiveOrCatch(v, new AckMessage(), n.getChannel("u", "v"));
		assertEquals(2, n.getChannel("v", "x").getContent().size());
		assertTrue(n.getChannel("v", "x").getContent().toArray()[1] instanceof AckMessage);
	}

	// ===== Full simulations of the algorithm =====

	/**
	 * simulationTest1:
	 * Test network 1 should return deadlock (false = not free).
	 */
	@Test
	void simulationTest1() {
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(network1().simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		assertEquals(1, output.get("u").size());
		assertEquals("false", output.get("u").iterator().next());
	}

	/**
	 * simulationTest2:
	 * Test network 2 should return NO deadlock (true = free).
	 */
	@Test
	void simulationTest2() {
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(network2().simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		assertEquals(1, output.get("u").size());
		assertEquals("true", output.get("u").iterator().next());
	}

	/**
	 * simulationTest3:
	 * Test network 3 should return NO deadlock (true = free).
	 */
	@Test
	void simulationTest3() {
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(network3().simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		assertEquals(1, output.get("u").size());
		assertEquals("true", output.get("u").iterator().next());
	}

	/**
	 * simulationTest4:
	 * Test network 4 should return NO deadlock (true = free).
	 */
	@Test
	void simulationTest4() {
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(network4().simulate(output));
		} catch (IllegalReceiveException e) {
			assertTrue(false);
		}

		assertEquals(1, output.get("u").size());
		assertEquals("true", output.get("u").iterator().next());
	}

	/**
	 * We run a simulation, but explicitly check that
	 * each message is correctly reacted upon.
	 * We use Test network 5 with u as initiator.
	 * At the end, process u should be aware
	 * that there is no deadlock.
	 * 
	 * Test by Guillaume Coigniez
	 */
	@Test
	void extraDetailedSimulationTest1() {
		Network n = network5();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		BrachaTouegProcess w = (BrachaTouegProcess) n.getProcess("w");
		BrachaTouegProcess x = (BrachaTouegProcess) n.getProcess("x");

		u.init();

		// u sends notify to v,x
		assertTrue(n.getChannel("u", "v").getContent().iterator().next() instanceof NotifyMessage);
		assertTrue(n.getChannel("u", "x").getContent().iterator().next() instanceof NotifyMessage);

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("u", "v"));
		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));

		// v notifies w
		assertTrue(n.getChannel("v", "w").getContent().iterator().next() instanceof NotifyMessage);
		receiveOrCatch(w, new NotifyMessage(), n.getChannel("v", "w"));

		// x grants u,w
		assertTrue(n.getChannel("x", "u").getContent().iterator().next() instanceof GrantMessage);
		assertTrue(n.getChannel("x", "w").getContent().iterator().next() instanceof GrantMessage);

		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		receiveOrCatch(w, new GrantMessage(), n.getChannel("x", "w"));

		// w notifies x
		assertTrue(n.getChannel("w", "x").getContent().iterator().next() instanceof NotifyMessage);
		receiveOrCatch(x, new NotifyMessage(), n.getChannel("w", "x"));

		// x sends done to w
		assertTrue(n.getChannel("x", "w").getContent().toArray()[1] instanceof DoneMessage);
		receiveOrCatch(w, new DoneMessage(), n.getChannel("x", "w"));

		// u acks x
		assertTrue(n.getChannel("u", "x").getContent().toArray()[1] instanceof AckMessage);
		receiveOrCatch(x, new AckMessage(), n.getChannel("u", "x"));

		// w grants v
		assertTrue(n.getChannel("w", "v").getContent().iterator().next() instanceof GrantMessage);
		receiveOrCatch(v, new GrantMessage(), n.getChannel("w", "v"));

		// v grants u
		assertTrue(n.getChannel("v", "u").getContent().iterator().next() instanceof GrantMessage);
		receiveOrCatch(u, new GrantMessage(), n.getChannel("v", "u"));

		// u acks v
		assertTrue(n.getChannel("u", "v").getContent().toArray()[1] instanceof AckMessage);
		receiveOrCatch(v, new AckMessage(), n.getChannel("u", "v"));

		// v acks w
		assertTrue(n.getChannel("v", "w").getContent().toArray()[1] instanceof AckMessage);
		receiveOrCatch(w, new AckMessage(), n.getChannel("v", "w"));

		// w acks x
		assertTrue(n.getChannel("w", "x").getContent().toArray()[1] instanceof AckMessage);
		receiveOrCatch(x, new AckMessage(), n.getChannel("w", "x"));

		// w sends done to v
		assertTrue(n.getChannel("w", "v").getContent().toArray()[1] instanceof DoneMessage);
		receiveOrCatch(v, new DoneMessage(), n.getChannel("w", "v"));

		// v sends done to u
		assertTrue(n.getChannel("v", "u").getContent().toArray()[1] instanceof DoneMessage);
		receiveOrCatch(u, new DoneMessage(), n.getChannel("v", "u"));

		// x sends done to u
		assertTrue(n.getChannel("x", "u").getContent().toArray()[1] instanceof DoneMessage);
		receiveOrCatch(u, new DoneMessage(), n.getChannel("x", "u"));

		// check if u indeed terminated and saw that there is no deadlock
		assertEquals(1, getPrinted(u).size());
		assertEquals("true", getPrinted(u).iterator().next());
	}
}
