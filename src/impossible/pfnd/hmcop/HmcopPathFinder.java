package impossible.pfnd.hmcop;

import java.util.List;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.dkstr.DijkstraPathFinder;

public class HmcopPathFinder implements ConstrainedPathFinder {

	private final double lambda;

	public HmcopPathFinder(double lambda) {
		this.lambda = lambda;
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		int numMetrics = graph.getNumMetrics();

		// Reverse search.
		HmcopRevRelaxation revRelaxation = new HmcopRevRelaxation(constraints);
		PathFinder revFinder = new DijkstraPathFinder(revRelaxation);

		if (revFinder.find(graph, from, to) == null) {
			return null;
		}

		if (revRelaxation.failureCondition(from, numMetrics)) {
			return null;
		}

		// Look-ahead search.
		HmcopLaRelaxation laRelaxation = new HmcopLaRelaxation(revRelaxation,
				constraints, lambda);

		PathFinder laFinder = new DijkstraPathFinder(laRelaxation);

		Path result = laFinder.find(graph, from, to);
		if (result == null) {
			return null;
		}

		if (laRelaxation.failureCondition(to, numMetrics)) {
			return null;
		}

		return result;
	}

}
