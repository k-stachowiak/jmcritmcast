package impossible.tfind.paggr;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.model.Tree;
import impossible.pfnd.PathFinder;
import impossible.tfind.SteinerTreeFinder;

import java.util.ArrayList;
import java.util.List;

public class PathAggrTreeFinder implements SteinerTreeFinder {

	private final List<Double> constraints;
	private final PathFinder pathFinder;
	private final ConstraintsComparer constraintsComparer;
	private final PathAggregator pathAggregator;

	public PathAggrTreeFinder(List<Double> constraints, PathFinder pathFinder,
			ConstraintsComparer constraintsComparer,
			PathAggregator pathAggregator) {
		this.constraints = constraints;
		this.pathFinder = pathFinder;
		this.constraintsComparer = constraintsComparer;
		this.pathAggregator = pathAggregator;
	}

	@Override
	public Tree find(Graph graph, List<Node> group) {

		// Process input
		Node source = group.get(0);
		List<Node> destinations = new ArrayList<>();
		for (int i = 1; i < group.size(); ++i)
			destinations.add(group.get(i));

		// Find paths
		List<Path> paths = new ArrayList<>();
		for (Node destination : destinations) {
			Path path = pathFinder.find(graph, source, destination);
			if (path == null
					|| !constraintsComparer.fulfilsConstraints(path,
							constraints))
				return null;
			paths.add(path);
		}

		// Aggregate paths
		Tree result = pathAggregator.aggregate(graph, source, paths);

		return result;
	}
}
