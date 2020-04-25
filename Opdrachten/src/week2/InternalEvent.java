package week2;

import framework.Process;

public class InternalEvent extends Event {

	private Process p;
	private String op;
	
	public InternalEvent(Process p, String op) {
		this.p = p;
		this.op = op;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof InternalEvent) {
			InternalEvent that = (InternalEvent) o;
			return this.p.equals(that.p) && this.op.equals(that.op);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return op.hashCode();
	}
	
	@Override
	public String toString() {
		return op + "@" + p;
	}

	public String getOp() {
		return op;
	}
	
	@Override
	public Process getProcess() {
		return p;
	}
}
