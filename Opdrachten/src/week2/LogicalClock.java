package week2;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogicalClock<T> {

	private Map<Event, T> timestamps = new LinkedHashMap<>();
	
	public void addTimestamp(Event e, T t) {
		if (timestamps.containsKey(e)) {
			throw new IllegalStateException();
		}
		
		timestamps.put(e, t);
	}
	
	public boolean containsTimestamp(Event e) {
		return timestamps.containsKey(e);
	}
	
	public T getTimestamp(Event e) {
		if (!timestamps.containsKey(e)) {
			throw new IllegalStateException();
		}
		
		return timestamps.get(e);
	}
	
	public Map<Event, T> getTimestamps() {
		return new LinkedHashMap<>(timestamps);
	}
}
