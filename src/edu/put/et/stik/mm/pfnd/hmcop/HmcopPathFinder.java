package edu.put.et.stik.mm.pfnd.hmcop;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.pfnd.PathFinder;
import edu.put.et.stik.mm.pfnd.dkstr.DijkstraPathFinder;

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

		if (revFinder.find(graph, to, from) == null) {
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
