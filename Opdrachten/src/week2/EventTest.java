//package week2;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//
//import framework.Network;
//
//class EventTest {
//
//	@Test
//	void parseTest1() {
//		String s = "s(p,q,1)";
//		Network n = new Network(true);
//
//		SendEvent e1 = (SendEvent) Event.parse(s, n);
//		SendEvent e2 = (SendEvent) Event.parse(s, n);
//
//		assertFalse(e1 == e2);
//		assertEquals(e1, e2);
//		assertEquals(e1.hashCode(), e2.hashCode());
//		assertEquals(s, e1.toString());
//	}
//
//	@Test
//	void parseTest2() {
//		String s = "r(p,q,1)";
//		Network n = new Network(true);
//
//		ReceiveEvent e1 = (ReceiveEvent) Event.parse(s, n);
//		ReceiveEvent e2 = (ReceiveEvent) Event.parse(s, n);
//
//		assertFalse(e1 == e2);
//		assertEquals(e1, e2);
//		assertEquals(e1.hashCode(), e2.hashCode());
//		assertEquals(s, e1.toString());
//	}
//
//	@Test
//	void parseTest3() {
//		String s = "a@p";
//		Network n = new Network(true);
//
//		InternalEvent e1 = (InternalEvent) Event.parse(s, n);
//		InternalEvent e2 = (InternalEvent) Event.parse(s, n);
//
//		assertFalse(e1 == e2);
//		assertEquals(e1, e2);
//		assertEquals(e1.hashCode(), e2.hashCode());
//		assertEquals(s, e1.toString());
//	}
//
//	@Test
//	void parseListTest1() {
//		String s = "s(p,q,1) r(p,q,1) a@p";
//		Network n = new Network(true);
//
//		List<Event> expected = Arrays.asList(Event.parse("s(p,q,1)", n), Event.parse("r(p,q,1)", n), Event.parse("a@p", n));
//		assertEquals(expected, Event.parseList(s, n));
//	}
//}
