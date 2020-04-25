package framework;

public class DefaultProcess extends Process {

	public DefaultProcess() {
	}

	public DefaultProcess(String name) {
		super.setName(name);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void init() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		throw new UnsupportedOperationException();
	}
}
