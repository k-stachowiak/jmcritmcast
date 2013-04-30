package pfnd.hmcp;


import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinder;
import pfnd.PathFinderFactory;


public class HmcpPathFinder implements ConstrainedPathFinder {

	PathFinderFactory pathFinderFactory;

	public HmcpPathFinder(PathFinderFactory pathFinderFactory) {
		this.pathFinderFactory = pathFinderFactory;
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		// Search reverse.
		// ---------------
		ReverseHmcpDijkstraRelaxation reverseDijkstraRelaxation = new ReverseHmcpDijkstraRelaxation(
				constraints);

		PathFinder reversePathFinder = pathFinderFactory
				.createDijkstra(reverseDijkstraRelaxation);

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
				.createDijkstra(lookAheadDijkstraRelaxation);

		Path path = lookAheadPathFinder.find(graph, from, to);
		if (path == null)
			return null;

		if (!lookAheadDijkstraRelaxation.constraintsFulfilled(to,
				graph.getNumMetrics()))
			return null;

		return path;
	}
}
