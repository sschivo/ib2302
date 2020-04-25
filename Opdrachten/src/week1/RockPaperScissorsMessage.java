package week1;

import framework.Message;

public class RockPaperScissorsMessage implements Message {

	private Item item;
	
	public RockPaperScissorsMessage(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}

	@Override
	public String toString() {
		if (item == Item.ROCK) {
			return "<rock>";
		} else if (item == Item.PAPER) {
			return "<paper>";
		} else {
			return "<scissors>";
		}
	}
}
