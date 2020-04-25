package week2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import framework.Channel;
import framework.DefaultMessage;
import framework.DefaultProcess;
import framework.Message;
import framework.Network;
import framework.Process;

public abstract class Event {

	public abstract Process getProcess();

	public static Event parse(String s, Network n) {
		
		BiFunction<String, Network, Event> parseSendOrReceiveEvent = new BiFunction<String, Network, Event>() {
			@Override
			public Event apply(String s, Network n) {
				if (s.indexOf("(") == -1 || s.lastIndexOf(")") != s.length() - 1 || s.indexOf("(") != s.lastIndexOf("(")
						|| s.indexOf(")") != s.lastIndexOf(")")) {
					throw new IllegalArgumentException(s);
				}

				String op = s.substring(0, s.indexOf("("));

				s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
				String[] tokens = s.split(",");
				if (tokens.length != 3) {
					throw new IllegalArgumentException();
				}

				String nameSender = tokens[0];
				String nameReceiver = tokens[1];
				
				if (!n.containsProcess(nameSender)) {
					n.addProcess(new DefaultProcess(nameSender));
				}
				if (!n.containsProcess(nameReceiver)) {
					n.addProcess(new DefaultProcess(nameReceiver));
				}
				if (!n.containsChannel(nameSender, nameReceiver)) {
					n.addChannel(nameSender, nameReceiver);
				}

				Channel c = n.getChannel(nameSender, nameReceiver);
				Message m = new DefaultMessage(tokens[2]);

				switch (op) {
				case "s":
				case "send":
					return new SendEvent(c, m);
				case "r":
				case "recv":
				case "receive":
					return new ReceiveEvent(c, m);
				default:
					throw new IllegalArgumentException();
				}
			}
		};

		BiFunction<String, Network, Event> parseInternalEvent = new BiFunction<String, Network, Event>() {
			@Override
			public Event apply(String s, Network n) {
				if (s.indexOf("@") == -1 || s.indexOf("@") != s.lastIndexOf("@")) {
					throw new IllegalArgumentException(s);
				}
				
				String[] tokens = s.split("@");
				if (tokens.length != 2) {
					throw new IllegalArgumentException();
				}
				
				String messageId = tokens[0];
				String processName = tokens[1];
				
				if (!n.containsProcess(tokens[1])) {
					n.addProcess(new DefaultProcess(processName));
				}
				
				return new InternalEvent(n.getProcess(processName), messageId);
			}
		};
		
		if (s.indexOf("(") > -1) {
			switch (s.substring(0, s.indexOf("("))) {
			case "s":
			case "send":
			case "r":
			case "recv":
			case "receive":
				return parseSendOrReceiveEvent.apply(s, n);
			default:
				return parseInternalEvent.apply(s, n);
			}
		} else {
			return parseInternalEvent.apply(s, n);
		}
	}
	
	public static List<Event> parseList(String s, Network n) {
		List<Event> list = new ArrayList<>();
		
		String[] tokens = s.split(" ");
		for (String token : tokens) {
			list.add(parse(token, n));
		}
		
		return list;
	}
}
