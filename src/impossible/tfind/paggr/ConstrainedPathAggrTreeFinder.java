package impossible.tfind.paggr;

import impossible.helpers.PathAggregator;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.model.topology.Tree;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;

import java.util.ArrayList;
import java.util.List;

public class ConstrainedPathAggrTreeFinder implements
		MetricConstrainedSteinerTreeFinder {

	private final ConstrainedPathFinder pathFinder;
	private final PathAggregator pathAggregator;

	public ConstrainedPathAggrTreeFinder(ConstrainedPathFinder pathFinder,
			PathAggregator pathAggregator) {
		this.pathFinder = pathFinder;
		this.pathAggregator = pathAggregator;
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {				

		// Process input
		Node source = group.get(0);
		List<Node> destinations = new ArrayList<>();
		for (int i = 1; i < group.size(); ++i) {
			destinations.add(group.get(i));
		}

		// Find paths
		List<Path> paths = new ArrayList<>();
		for (Node destination : destinations) {
			
			Path path = pathFinder
					.find(graph, source, destination, constraints);
			
			if (path == null) {
				return null;
			}
			
			paths.add(path);
		}

		// Aggregate paths
		Tree result = pathAggregator.aggregate(graph, source, paths);

		return result;
	}
}
