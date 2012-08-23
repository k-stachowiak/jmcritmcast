package impossible.pfnd.hmcp;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;

import java.util.ArrayList;
import java.util.List;

public class HmcpPathFinder implements ConstrainedPathFinder {

	PathFinderFactory pathFinderFactory;
	private List<Double> constraints;

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

	@Override
	public void setConstraints(List<Double> constraints) {
		this.constraints = new ArrayList<>(constraints);
	}
}
