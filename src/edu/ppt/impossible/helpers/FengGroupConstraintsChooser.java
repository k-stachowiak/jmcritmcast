package edu.ppt.impossible.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Path;
import edu.ppt.impossible.pfnd.PathFinder;
import edu.ppt.impossible.pfnd.PathFinderFactory;

public class FengGroupConstraintsChooser implements GroupConstraintsChooser {

	private class MetricPair {
		private final double min;
		private final double max;

		public MetricPair(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}
	}

	private final double delta;
	private final PathFinderFactory pathFinderFactory;

	public FengGroupConstraintsChooser(double delta,
			PathFinderFactory pathFinderFactory) {

		this.delta = delta;
		this.pathFinderFactory = pathFinderFactory;
	}

	@Override
	public List<Double> choose(Graph graph, List<Node> group) {

		double M = graph.getNumMetrics();

		// Determine the extreme paths for the given metrics.
		// --------------------------------------------------
		Map<Integer, List<Path>> extremePathsMap = new HashMap<>();
		for (int m = 0; m < M; ++m) {

			Node root = group.get(0);
			PathFinder pathFinder = pathFinderFactory.CreateDijkstraIndex(m);

			for (int n = 1; n < group.size(); ++n) {
				Path path = pathFinder.find(graph, root, group.get(n));
				if (path == null)
					return null;

				if (!extremePathsMap.containsKey(m)) {
					extremePathsMap.put(Integer.valueOf(m),
							new ArrayList<Path>());
				}

				extremePathsMap.get(m).add(path);
			}
		}

		// Determine the extreme metrics.
		// ------------------------------
		Map<Integer, MetricPair> extremeMetrics = new HashMap<>();
		for (int m = 1; m < M; ++m) {
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;

			// Find the minimum value. It will be the maximal metric from the
			// paths optimized against the given metric.
			for (Path path : extremePathsMap.get(m)) {
				double metric = path.getMetrics().get(m);
				if (metric < min)
					min = metric;
			}

			// Find the maximum value. It will be the maximal metric from the
			// cost optimized paths.
			for (Path path : extremePathsMap.get(m)) {
				double metric = path.getMetrics().get(0);
				if (metric > max)
					max = metric;
			}
			extremeMetrics.put(Integer.valueOf(m), new MetricPair(min, max));
		}
		
		// Interpolate the extrema and return.
        // -----------------------------------
        List<Double> result = new ArrayList<>();
        for (Map.Entry<Integer, MetricPair> entry : extremeMetrics.entrySet())
        {
                double min = entry.getValue().getMin();
                double max = entry.getValue().getMax();
                result.add(min + delta * (max - min));
        }

        return result;
	}
}
