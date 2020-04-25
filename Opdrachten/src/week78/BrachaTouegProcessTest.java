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

	// Example 5.3
	// Deadlocked
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

	// Example 5.4
	// Free
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

	// Example 5.5
	// Free
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

	// Free (trivially)
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

	// Initiator initiates (notify)
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

	// Initiator initiates ((notify +) grant)
	// This only checks that the initiator actually waits for the ack messages, since it doesn't need any done messages.
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

	// Non-initiator is passive
	@Test
	void initTest3() {
		Network n = network1();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		assertEquals(0, n.getChannel("v", "u").getContent().size());
		assertEquals(0, n.getChannel("v", "w").getContent().size());
		assertEquals(0, n.getChannel("v", "x").getContent().size());
	}

	// ===== Exceptions =====
	// Illegal message type
	@Test
	void receiveTest1() {
		Network n = network3();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertThrows(IllegalReceiveException.class, () -> u.receive(Message.DUMMY, n.getChannel("v", "u")));
	}
	@Test
	void receiveTest2() {
		Network n = network3();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		assertThrows(IllegalReceiveException.class, () -> v.receive(Message.DUMMY, n.getChannel("u", "v")));
	}

	// Unexpected done
	@Test
	void receiveTest3() {
		Network n = network3();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertThrows(IllegalReceiveException.class, () -> u.receive(new DoneMessage(), n.getChannel("w", "u")));
	}
	@Test
	void receiveTest4() {
		Network n = network3();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("u", "v"));

		assertThrows(IllegalReceiveException.class, () -> v.receive(new DoneMessage(), n.getChannel("x", "v")));
	}

	// Double done
	@Test
	void receiveTest5() {
		Network n = network3();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new DoneMessage(), n.getChannel("v", "u"));
		assertThrows(IllegalReceiveException.class, () -> u.receive(new DoneMessage(), n.getChannel("v", "u")));
	}
	@Test
	void receiveTest6() {
		Network n = network3();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("u", "v"));

		receiveOrCatch(v, new DoneMessage(), n.getChannel("w", "v"));
		assertThrows(IllegalReceiveException.class, () -> v.receive(new DoneMessage(), n.getChannel("w", "v")));
	}

	// Unexpected ack
	@Test
	void receiveTest7() {
		Network n = network4();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertThrows(IllegalReceiveException.class, () -> u.receive(new AckMessage(), n.getChannel("x", "u")));
	}
	@Test
	void receiveTest8() {
		Network n = network3();

		BrachaTouegProcess x = (BrachaTouegProcess) n.getProcess("x");
		x.init();

		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));

		assertThrows(IllegalReceiveException.class, () -> x.receive(new AckMessage(), n.getChannel("v", "x")));
	}

	// Double ack
	@Test
	void receiveTest9() {
		Network n = network4();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new AckMessage(), n.getChannel("v", "u"));
		assertThrows(IllegalReceiveException.class, () -> u.receive(new AckMessage(), n.getChannel("v", "u")));
	}
	@Test
	void receiveTest10() {
		Network n = network3();

		BrachaTouegProcess x = (BrachaTouegProcess) n.getProcess("x");
		x.init();

		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));

		receiveOrCatch(x, new AckMessage(), n.getChannel("u", "x"));
		assertThrows(IllegalReceiveException.class, () -> x.receive(new AckMessage(), n.getChannel("u", "x")));
	}

	// ===== Initiator =====
	// Receive notify, return done
	@Test
	void receiveTest11() {
		Network n = network2();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertEquals(0, n.getChannel("u", "v").getContent().size());

		receiveOrCatch(u, new NotifyMessage(), n.getChannel("v", "u"));

		assertEquals(1, n.getChannel("u", "v").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().iterator().next() instanceof DoneMessage);
	}

	// Terminate: deadlock
	@Test
	void receiveTest12() {
		Network n = network2();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new DoneMessage(), n.getChannel("x", "u"));
		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		assertEquals(0, getPrinted(u).size());

		receiveOrCatch(u, new DoneMessage(), n.getChannel("w", "u"));
		assertEquals(1, getPrinted(u).size());
		assertEquals("false", getPrinted(u).iterator().next());
	}

	// Terminate: free
	@Test
	void receiveTest13() {
		Network n = network2();

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		receiveOrCatch(u, new DoneMessage(), n.getChannel("x", "u"));
		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		receiveOrCatch(u, new AckMessage(), n.getChannel("v", "u"));
		assertEquals(0, getPrinted(u).size());

		receiveOrCatch(u, new DoneMessage(), n.getChannel("w", "u"));
		assertEquals(1, getPrinted(u).size());
		assertEquals("true", getPrinted(u).iterator().next());
	}

	// Receive grants
	@Test
	void receiveTest14() {
		Network n = network1();
		addRequest(n, "u", "v");

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		assertEquals(1, n.getChannel("u", "v").getContent().size());
		assertEquals(1, n.getChannel("u", "w").getContent().size());
		assertEquals(1, n.getChannel("u", "x").getContent().size());

		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		assertEquals(2, n.getChannel("u", "x").getContent().size());
		assertTrue(n.getChannel("u", "x").getContent().toArray()[1] instanceof AckMessage);
		assertEquals(1, n.getChannel("u", "v").getContent().size());

		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());
		assertEquals(2, n.getChannel("u", "v").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().toArray()[1] instanceof GrantMessage);

		receiveOrCatch(u, new GrantMessage(), n.getChannel("v", "u"));
		assertEquals(3, n.getChannel("u", "v").getContent().size());
		assertTrue(n.getChannel("u", "v").getContent().toArray()[2] instanceof AckMessage);
	}

	// Receive ack
	@Test
	void receiveTest15() {
		Network n = network1();
		addRequest(n, "x", "u");

		BrachaTouegProcess u = (BrachaTouegProcess) n.getProcess("u");
		u.init();

		receiveOrCatch(u, new GrantMessage(), n.getChannel("x", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());

		receiveOrCatch(u, new GrantMessage(), n.getChannel("w", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());

		receiveOrCatch(u, new AckMessage(), n.getChannel("x", "u"));
		assertEquals(1, n.getChannel("u", "w").getContent().size());

		receiveOrCatch(u, new AckMessage(), n.getChannel("v", "u"));
		assertEquals(2, n.getChannel("u", "w").getContent().size());
		assertTrue(n.getChannel("u", "w").getContent().toArray()[1] instanceof AckMessage);

		assertEquals(3, n.getChannel("u", "x").getContent().size());
		assertEquals(1, n.getChannel("u", "v").getContent().size());
	}

	// ===== Non-initiator =====
	// Receive notify
	@Test
	void receiveTest16() {
		Network n = network1();

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

		receiveOrCatch(w, new NotifyMessage(), n.getChannel("u", "w"));
		assertEquals(0, n.getChannel("w", "u").getContent().size());
		assertEquals(1, n.getChannel("w", "v").getContent().size());
		assertTrue(n.getChannel("w", "v").getContent().iterator().next() instanceof NotifyMessage);
		assertEquals(1, n.getChannel("w", "x").getContent().size());
		assertTrue(n.getChannel("w", "x").getContent().iterator().next() instanceof NotifyMessage);

		receiveOrCatch(x, new NotifyMessage(), n.getChannel("w", "x"));
		assertEquals(1, n.getChannel("x", "u").getContent().size());
		assertTrue(n.getChannel("x", "u").getContent().iterator().next() instanceof GrantMessage);
		assertEquals(1, n.getChannel("x", "v").getContent().size());
		assertTrue(n.getChannel("x", "v").getContent().iterator().next() instanceof GrantMessage);
		assertEquals(1, n.getChannel("x", "w").getContent().size());
		assertTrue(n.getChannel("x", "w").getContent().iterator().next() instanceof GrantMessage);

		receiveOrCatch(x, new NotifyMessage(), n.getChannel("u", "x"));
		assertEquals(2, n.getChannel("x", "u").getContent().size());
		assertTrue(n.getChannel("x", "u").getContent().toArray()[1] instanceof DoneMessage);
		assertEquals(1, n.getChannel("x", "v").getContent().size());
		assertEquals(1, n.getChannel("x", "w").getContent().size());
	}

	// Receive done
	@Test
	void receiveTest17() {
		Network n = network2();

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		BrachaTouegProcess w = (BrachaTouegProcess) n.getProcess("w");
		w.init();

		receiveOrCatch(w, new NotifyMessage(), n.getChannel("u", "w"));
		receiveOrCatch(v, new NotifyMessage(), n.getChannel("w", "v"));

		receiveOrCatch(v, new DoneMessage(), n.getChannel("u", "v"));
		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(0, n.getChannel("v", "w").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		receiveOrCatch(v, new DoneMessage(), n.getChannel("x", "v"));
		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "w").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());
		assertTrue(n.getChannel("v", "w").getContent().toArray()[0] instanceof DoneMessage);

		receiveOrCatch(w, new DoneMessage(), n.getChannel("x", "w"));
		assertEquals(0, n.getChannel("w", "u").getContent().size());
		assertEquals(1, n.getChannel("w", "v").getContent().size());
		assertEquals(1, n.getChannel("w", "x").getContent().size());

		receiveOrCatch(w, new DoneMessage(), n.getChannel("v", "w"));
		assertEquals(1, n.getChannel("w", "u").getContent().size());
		assertEquals(1, n.getChannel("w", "v").getContent().size());
		assertEquals(1, n.getChannel("w", "x").getContent().size());
		assertTrue(n.getChannel("w", "u").getContent().toArray()[0] instanceof DoneMessage);
	}

	// Receive grant
	@Test
	void receiveTest18() {
		Network n = network1();
		addRequest(n, "v", "w");

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("w", "v"));

		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "w").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		receiveOrCatch(v, new GrantMessage(), n.getChannel("u", "v"));
		assertEquals(2, n.getChannel("v", "u").getContent().size());
		assertTrue(n.getChannel("v", "u").getContent().toArray()[1] instanceof AckMessage);

		receiveOrCatch(v, new GrantMessage(), n.getChannel("x", "v"));
		assertEquals(1, n.getChannel("v", "x").getContent().size());
		assertEquals(2, n.getChannel("v", "w").getContent().size());
		assertTrue(n.getChannel("v", "w").getContent().toArray()[1] instanceof GrantMessage);

		receiveOrCatch(v, new GrantMessage(), n.getChannel("w", "v"));
		assertEquals(3, n.getChannel("v", "w").getContent().size());
		assertTrue(n.getChannel("v", "w").getContent().toArray()[2] instanceof AckMessage);
	}

	// Receive ack
	@Test
	void receiveTest19() {
		Network n = network1();
		addRequest(n, "u", "v");

		BrachaTouegProcess v = (BrachaTouegProcess) n.getProcess("v");
		v.init();

		receiveOrCatch(v, new NotifyMessage(), n.getChannel("w", "v"));
		assertEquals(1, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "x").getContent().size());
		
		receiveOrCatch(v, new GrantMessage(), n.getChannel("u", "v"));
		assertEquals(2, n.getChannel("v", "u").getContent().size());
		
		receiveOrCatch(v, new GrantMessage(), n.getChannel("x", "v"));
		assertEquals(3, n.getChannel("v", "u").getContent().size());
		assertEquals(1, n.getChannel("v", "w").getContent().size());

		receiveOrCatch(v, new AckMessage(), n.getChannel("w", "v"));
		assertEquals(1, n.getChannel("v", "x").getContent().size());

		receiveOrCatch(v, new AckMessage(), n.getChannel("u", "v"));
		assertEquals(2, n.getChannel("v", "x").getContent().size());
		assertTrue(n.getChannel("v", "x").getContent().toArray()[1] instanceof AckMessage);
	}

	// ===== Simulations =====
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
}
