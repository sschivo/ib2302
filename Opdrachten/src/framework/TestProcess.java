package framework;

public class TestProcess extends Process {

	public TestProcess() {
	}

	public TestProcess(String name) {
		super.setName(name);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void init() {
		System.out.println("init");
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		System.out.println("received " + m + " from " + c);
	}
}
