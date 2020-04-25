package week56;

import framework.Process;

public abstract class WaveProcess extends Process {

	private boolean active = true;

	public void done() {
		if (!active) {
			throw new IllegalStateException();
		}

		active = false;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isPassive() {
		return !active;
	}
}
