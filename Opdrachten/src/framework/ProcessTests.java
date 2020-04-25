package framework;

import java.util.List;

public class ProcessTests {
	
	public static void receiveOrCatch(Process p, Message m, Channel c) {
		try {
			p.receive(m, c);
		} catch (IllegalReceiveException e) {
		}
	}
	
	public static List<String> getPrinted(Process p) {
		return p.printed;
	}
}
