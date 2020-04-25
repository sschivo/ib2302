package week2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import framework.Network;
import framework.Process;

public class VectorClock extends LogicalClock<Map<Process, Integer>> {

	public VectorClock(Map<Process, List<Event>> sequences) {
		// TODO
	}
	
	/*
	 * -------------------------------------------------------------------------
	 */
	
	public static Map<Process, Integer> parseTimestamp(String s, Network n) {
		String[] tokens = s.split(",");
		List<Process> processes = new ArrayList<>(n.getProcesses().values());
		if (tokens.length != processes.size()) {
			throw new IllegalArgumentException();
		}
		
		Map<Process, Integer> timestamp = new LinkedHashMap<>();
		
		for (int i = 0; i< tokens.length; i++) {
			try {
				timestamp.put(processes.get(i), Integer.parseInt(tokens[i]));
			} catch (Throwable t) {
				throw new IllegalArgumentException();
			}
		}
		
		return timestamp;
	}
}
