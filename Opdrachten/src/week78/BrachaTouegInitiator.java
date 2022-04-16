package week78;

import java.util.LinkedHashSet;
import java.util.Set;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import week1.RockPaperScissorsMessage;
import week56.TokenMessage;
import framework.SetChannel;

public class BrachaTouegInitiator extends BrachaTouegProcess {
	public NotifyMessage m;
	public GrantMessage g;
	Set<Channel> notified = new LinkedHashSet<>();
	Set<Channel> granted = new LinkedHashSet<>();
	Set<Channel> done = new LinkedHashSet<>();
	Set<Channel> doneSent = new LinkedHashSet<>();
	
	Set<Channel> noti = new LinkedHashSet<>();

	Channel notifier;
	public boolean notifiedB = false;
	Set<Channel> acks = new LinkedHashSet<>();
	Set<Channel> acksSent = new LinkedHashSet<>();
	
	@Override
	public void init() {
		System.out.println(this.requests);
		System.out.println(this.outRequests);
		System.out.println(this.inRequests);
		System.out.println(this.getOutgoing());
		System.out.println(notified);
		
		m = new NotifyMessage();
		g = new GrantMessage();

		notifiedB = true;
		for (Channel i : outRequests) {
			send(m, i);
			notified.add(i);
		}
		if (this.requests == 0) {
			for (Channel i : this.inRequests) {
				for (Channel j : this.getOutgoing()) {
					if (i.getSender().toString().equals(j.getReceiver().toString()) 
							&& i.getReceiver().toString().equals(j.getSender().toString())) {
						send(g, j);
						
						granted.add(j);
					}
				}
				
			}
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		System.out.println("notified.size");
		System.out.println(notified.size());
		System.out.println("done.size");
		System.out.println(done.size());
		System.out.println("requests");
		System.out.println(requests);
		int in = 0;
		int an = 0;
		if (m == Message.DUMMY) {
			
			System.out.println("fffff");
			throw new IllegalReceiveException();
		}
		if (m instanceof DoneMessage) {
			if (notified.isEmpty()) {
				System.out.println("gggggggg");
				throw new IllegalReceiveException();
			}
			else  {
				for (Channel j : notified) {
					if (c.getSender().toString().equals(j.getReceiver().toString()) 
							&& c.getReceiver().toString().equals(j.getSender().toString())) {
						in = 1;
					
					}
				}
				if (in == 0) {
					System.out.println("hhhhhhh");
					throw new IllegalReceiveException();
				}
				System.out.println("add");
				if (done.contains(c)) {
					System.out.println("dit?");
					throw new IllegalReceiveException();
				}
				done.add(c);
				System.out.println(done.size());
				
			
				if (done.size() == notified.size()) {
					System.out.println("equalsize");
					System.out.println(requests);
					if (requests < 0) {
						this.print("true");
					}
					else this.print("false");
				}
				 
				
			}
			
		}
		if (m instanceof GrantMessage) {
			
			requests -=1;
if (requests < 0) {
				
				for (Channel j : outRequests) {
					if (c.getSender().toString().equals(j.getReceiver().toString()) 
							&& c.getReceiver().toString().equals(j.getSender().toString())) {
						AckMessage d = new AckMessage();
						send(d,j);
						acksSent.add(j);
					
					}
				}
				
			}
			if (requests == 0) {
				System.out.println("gotgrant");
				requests -=1;
				GrantMessage d = new GrantMessage();
				for (Channel j : inRequests) {
					for (Channel s : this.getOutgoing()) {
						if (s.getSender().toString().equals(j.getReceiver().toString()) 
								&& s.getReceiver().toString().equals(j.getSender().toString())) {
							if (!granted.contains(s)) {
								send(d,s);
								granted.add(s);
								}
						
						}
					}
				}
				
			}
			
			
			System.out.println("acks");
			System.out.println(acks);

			System.out.println("granted");
			System.out.println(granted);
			if (acks.size() == granted.size()) {
				for (Channel j : outRequests) {
					if (c.getSender().toString().equals(j.getReceiver().toString()) 
							&& c.getReceiver().toString().equals(j.getSender().toString())) {
						AckMessage d = new AckMessage();
						send(d,j);
						acksSent.add(j);
					
					}
				}
			}
			
		}
		if (m instanceof AckMessage) {
			System.out.println("granted");
			System.out.println(granted);
			
			System.out.println("acks");
			System.out.println(acks);
			
			if (granted.isEmpty()) {
				System.out.println("iiiiiiiii");
				throw new IllegalReceiveException();
			}
			else {
				
				for (Channel j : granted) {
					if (c.getSender().toString().equals(j.getReceiver().toString()) 
							&& c.getReceiver().toString().equals(j.getSender().toString())) {
						an = 1;
					
					}
				}
				if (an == 0) {
					System.out.println("jjjjjjj");
					throw new IllegalReceiveException();
				}if (acks.contains(c)) {
					System.out.println("eeeeeee");
					throw new IllegalReceiveException();
					
				}
				acks.add(c);
				System.out.println("inreq");
				System.out.println(inRequests);
				
				System.out.println("acks");
				System.out.println(acks);
				if (acks.size() == granted.size()) {
					for (Channel j : outRequests) {
						if (!(acksSent.contains(j))) {
							AckMessage d = new AckMessage();
							send(d,j);
							acksSent.add(j);
						}
						
					}
					
				}
				
			}
			if (acks.size()== granted.size()&& requests <=0 && notified.size()== done.size()) {
				System.out.println("trueeeeeee");
				this.print("true");
			}
				
			
		}
		if (m instanceof NotifyMessage) {
			noti.add(c);
			if (notifiedB == false) {
				notifiedB = true;
				notifier = c;
				
			}
			else {
				DoneMessage d = new DoneMessage();
				for (Channel j : this.getOutgoing()) {
					if (j.getSender().toString().equals(c.getReceiver().toString()) 
							&& j.getReceiver().toString().equals(c.getSender().toString())) {
						if (!doneSent.contains(j)) {
							DoneMessage k = new DoneMessage();
							send(k,j);
							doneSent.add(j);
							}
					}
					
				}
			}
		}
	}
}
