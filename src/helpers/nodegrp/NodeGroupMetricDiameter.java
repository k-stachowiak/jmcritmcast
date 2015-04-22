package helpers.nodegrp;

import helpers.metrprov.MetricProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;
import pfnd.CommonRelaxationImpl;
import pfnd.PathFinder;
import pfnd.PathFinderFactory;

public class NodeGroupMetricDiameter implements NodeGroupMetric {

	private static double pathCost(Path p, MetricProvider mp) {
		double result = 0;
		for (Edge e : p.getEdges()) {
			result += mp.get(e);
		}
		return result;
	}

	private class PathDistanceComparator implements Comparator<Path> {

		private final MetricProvider metricProvider;

		public PathDistanceComparator(MetricProvider metricProvider) {
			this.metricProvider = metricProvider;
		}

		@Override
		public int compare(Path x, Path y) {
			return Double.compare(pathCost(x, metricProvider),
					pathCost(y, metricProvider));
		}
	}

	private final MetricProvider metricProvider;
	private final PathDistanceComparator pathDistanceComparator;
	private final PathFinder finder;

	public NodeGroupMetricDiameter(PathFinderFactory finderFactory,
			MetricProvider metricProvider) {

		this.metricProvider = metricProvider;
		this.pathDistanceComparator = new PathDistanceComparator(metricProvider);
		this.finder = finderFactory.createDijkstra(new CommonRelaxationImpl(
				metricProvider));
	}

	@Override
	public double get(List<Node> group, Graph graph) {
		List<Path> pathCosts = new ArrayList<>();
		for (int i = 0; i < group.size() - 1; ++i) {
			Node u = group.get(i);
			for (int j = i + 1; j < group.size(); ++j) {
				Node v = group.get(j);
				Path p = finder.find(graph, u, v);
				pathCosts.add(p);
			}
		}
		pathCosts.sort(pathDistanceComparator);
		return pathCost(pathCosts.get(pathCosts.size() - 1), metricProvider);
	}
}
