package framework;

/**
 * A message and the channel it is delivered on.
 */
public class ChannelMessage {
	public final Message message;
	public final Channel channel;

	public ChannelMessage(Message message, Channel channel) {
		this.message = message;
		this.channel = channel;
	}
}
