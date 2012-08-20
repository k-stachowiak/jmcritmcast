package edu.ppt.impossible.pfnd.hmcp;

import java.util.List;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Path;
import edu.ppt.impossible.pfnd.PathFinder;
import edu.ppt.impossible.pfnd.PathFinderFactory;

public class HmcpPathFinder implements PathFinder {

	PathFinderFactory pathFinderFactory;
	private final List<Double> constraints;

	public HmcpPathFinder(PathFinderFactory pathFinderFactory,
			List<Double> constraints) {
		this.pathFinderFactory = pathFinderFactory;
		this.constraints = constraints;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		// Search reverse.
		// ---------------
		ReverseHmcpDijkstraRelaxation reverseDijkstraRelaxation = new ReverseHmcpDijkstraRelaxation(
				constraints);

		PathFinder reversePathFinder = pathFinderFactory
				.CreateDijkstra(reverseDijkstraRelaxation);

		if (reversePathFinder.find(graph, to, from) == null)
			return null;

		if (reverseDijkstraRelaxation.guaranteedFailure(from,
				graph.getNumMetrics()))
			return null;

		// Search look ahead.
		// ------------------
		LookAheadHmcpDijkstraRelaxation lookAheadDijkstraRelaxation = new LookAheadHmcpDijkstraRelaxation(
				constraints, reverseDijkstraRelaxation);

		PathFinder lookAheadPathFinder = pathFinderFactory
				.CreateDijkstra(lookAheadDijkstraRelaxation);

		Path path = lookAheadPathFinder.find(graph, from, to);
		if (path == null)
			return null;

		if (!lookAheadDijkstraRelaxation.constraintsFulfilled(to,
				graph.getNumMetrics()))
			return null;

		return path;
	}
}
