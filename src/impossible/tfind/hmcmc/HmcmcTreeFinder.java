package impossible.tfind.hmcmc;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.model.topology.Tree;
import impossible.pfnd.CommonRelaxation;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;

import java.util.ArrayList;
import java.util.List;

public class HmcmcTreeFinder implements MetricConstrainedSteinerTreeFinder {

	private final ConstraintsComparer constraintsComparer;
	private final PathFinderFactory pathFinderFactory;
	private final PathAggregator pathAggregator;

	public HmcmcTreeFinder(ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator) {
		this.constraintsComparer = constraintsComparer;
		this.pathFinderFactory = pathFinderFactory;
		this.pathAggregator = pathAggregator;
	}

	public Tree find(Graph graph, List<Node> spanned, List<Double> constraints) {

		Node source = spanned.get(0);

		// Partial search.
		// ---------------
		CommonRelaxation partialRelaxation = new PartialDijkstraRelaxation(
				constraints);

		PathFinder partialFinder = pathFinderFactory
				.createDijkstra(partialRelaxation);

		// Perform any find from source to setup the labels.
		if (partialFinder.find(graph, source, spanned.get(1)) == null)
			return null;

		// Store partial paths.
		List<Path> paths = new ArrayList<>();
		List<Node> failedDestinations = new ArrayList<>();
		for (int d = 1; d < spanned.size(); ++d) {
			Path path = partialRelaxation.buildPath(graph, source,
					spanned.get(d));

			if (path == null
					|| !constraintsComparer.fulfilsAll(path, constraints))
				failedDestinations.add(spanned.get(d));
			else
				paths.add(path);
		}

		// Immediate success.
		// ------------------
		if (failedDestinations.isEmpty())
			return pathAggregator.aggregate(graph, source, paths);

		// Try optimizing.
		// ---------------
		ConstrainedPathFinder hmcp = pathFinderFactory.createHmcp();
		for (Node failed : failedDestinations) {
			Path path = hmcp.find(graph, source, failed, constraints);

			if (path == null
					|| !constraintsComparer.fulfilsAll(path, constraints))
				return null;

			paths.add(path);
		}

		return pathAggregator.aggregate(graph, source, paths);
	}
}
