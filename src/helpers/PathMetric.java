package helpers;

import java.util.Comparator;

public class PathMetric {

	public static class CostComparer implements Comparator<PathMetric> {

		@Override
		public int compare(PathMetric x, PathMetric y) {
			return Double.compare(x.getCost(), y.getCost());
		}

	}

	public static class HopComparer implements Comparator<PathMetric> {

		@Override
		public int compare(PathMetric x, PathMetric y) {
			return Double.compare(x.getHop(), y.getHop());
		}

	}

	private final double hop;
	private final double cost;

	public PathMetric(double hop, double cost) {
		this.hop = hop;
		this.cost = cost;
	}

	public double getHop() {
		return hop;
	}

	public double getCost() {
		return cost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(hop);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		PathMetric other = (PathMetric) obj;
		if (Double.doubleToLongBits(cost) != Double
				.doubleToLongBits(other.cost))
			return false;
		if (Double.doubleToLongBits(hop) != Double.doubleToLongBits(other.hop))
			return false;
		return true;
	}
}
