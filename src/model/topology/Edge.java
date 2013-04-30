package model.topology;

import java.util.List;

public class Edge {

	private final int from;
	private final int to;
	private final List<Double> metrics;

	public Edge(int from, int to, List<Double> metrics) {
		super();
		this.from = from;
		this.to = to;
		this.metrics = metrics;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public List<Double> getMetrics() {
		return metrics;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(from);
		sb.append(" -> ");
		sb.append(to);
		sb.append(" {");
		for(Double m : metrics) {
			sb.append(m);
			sb.append(' ');
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from;
		result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
		result = prime * result + to;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (from != other.from)
			return false;
		if (metrics == null) {
			if (other.metrics != null)
				return false;
		} else if (!metrics.equals(other.metrics))
			return false;
		if (to != other.to)
			return false;
		return true;
	}
}
