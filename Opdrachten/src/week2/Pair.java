package week2;

class Pair {
	private Event left;
	private Event right;

	Pair(Event left, Event right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair that = (Pair) o;
			return this.left.equals(that.left) && this.right.equals(that.right);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return left.hashCode() + right.hashCode();
	}

	@Override
	public String toString() {
		return left + "<" + right;
	}
	
	public Event getLeft() {
		return left;
	}
	
	public Event getRight() {
		return right;
	}
}