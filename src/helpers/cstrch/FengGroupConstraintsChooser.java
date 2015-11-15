package helpers.cstrch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;
import pfnd.PathFinder;
import pfnd.PathFinderFactory;

public class FengGroupConstraintsChooser implements GroupConstraintsChooser {

	public static class Range {
		private final double min;
		private final double max;

		public Range(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(max);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(min);
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
			Range other = (Range) obj;
			if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
				return false;
			if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Range [min=" + min + ", max=" + max + "]";
		}

	}

	private final double delta;
	private final PathFinderFactory pathFinderFactory;

	public FengGroupConstraintsChooser(double delta, PathFinderFactory pathFinderFactory) {

		this.delta = delta;
		this.pathFinderFactory = pathFinderFactory;
	}

	@Override
	public List<Double> choose(Graph graph, List<Node> group) {
		Map<Integer, Double> mins = computeMins(graph, group);
		Map<Integer, Double> maxes = computeMaxes(graph, group);
		List<Double> result = new ArrayList<>();
		for (int m = 1; m < graph.getNumMetrics(); ++m) {
			double min = mins.get(m);
			double max = maxes.get(m);
			result.add(min + delta * (max - min));
		}
		return result;
	}

	public List<Range> chooseRanges(Graph graph, List<Node> group) {
		Map<Integer, Double> mins = computeMins(graph, group);
		Map<Integer, Double> maxes = computeMaxes(graph, group);
		List<Range> result = new ArrayList<>();
		for (int m = 1; m < graph.getNumMetrics(); ++m) {
			double min = mins.get(m);
			double max = maxes.get(m);
			result.add(new Range(min, max));
		}
		return result;
	}

	private Map<Integer, Double> computeMins(Graph graph, List<Node> group) {

		int numMetrics = graph.getNumMetrics();
		Node from = group.get(0);

		// Initialize result with positive infinities
		Map<Integer, Double> mins = new HashMap<>();
		for (int m = 1; m < numMetrics; ++m) {
			mins.put(m, Double.POSITIVE_INFINITY);
		}

		// Analyze paths to each destination
		for (int destinationIndex = 1; destinationIndex < group.size(); ++destinationIndex) {

			Node to = group.get(destinationIndex);

			// Check if any of the metrics is greater than current max
			for (int m = 1; m < numMetrics; ++m) {

				PathFinder pathFinder = pathFinderFactory.createDijkstraIndex(m);
				Path path = pathFinder.find(graph, from, to);
				List<Double> metrics = path.getMetrics();

				double metric = metrics.get(m);
				double min = mins.get(m);

				if (metric < min) {
					mins.put(m, metric);
				}
			}
		}

		return mins;
	}

	private Map<Integer, Double> computeMaxes(Graph graph, List<Node> group) {

		int numMetrics = graph.getNumMetrics();
		PathFinder pathFinder = pathFinderFactory.createDijkstraIndex(0);
		Node from = group.get(0);

		// Initialize result with negative infinities
		Map<Integer, Double> maxes = new HashMap<>();
		for (int m = 1; m < numMetrics; ++m) {
			maxes.put(m, Double.NEGATIVE_INFINITY);
		}

		// Analyze paths to each destination
		for (int destinationIndex = 1; destinationIndex < group.size(); ++destinationIndex) {

			Node to = group.get(destinationIndex);
			Path path = pathFinder.find(graph, from, to);

			List<Double> metrics = path.getMetrics();

			// Check if any of the metrics is greater than current max
			for (int m = 1; m < numMetrics; ++m) {

				double metric = metrics.get(m);
				double max = maxes.get(m);

				if (metric > max) {
					maxes.put(m, metric);
				}
			}
		}

		return maxes;
	}
}
