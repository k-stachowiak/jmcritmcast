package impossible.tfind.rdp;

import impossible.model.topology.Edge;

public class Event implements Comparable<Event> {

	private final int signalId;
	private final double time;
	private final Edge edge;
	
	public Event(int signalId, double time, Edge edge) {
		super();
		this.signalId = signalId;
		this.time = time;
		this.edge = edge;
	}

	public int getSignalId() {
		return signalId;
	}

	public double getTime() {
		return time;
	}

	public Edge getEdge() {
		return edge;
	}

	@Override
	public int compareTo(Event other) {
		return Double.compare(time, other.time);
	}

}
