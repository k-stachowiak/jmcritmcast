package pfnd.lbpsa;


import helpers.ConstraintsComparer;

import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;


public class LbpsaPathFinder implements ConstrainedPathFinder {

	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	public LbpsaPathFinder(PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer) {
		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		LbpsaFeasibleFinder feasibleFinder = new LbpsaFeasibleFinder(
				pathFinderFactory, constraintsComparer);

		Path feasiblePath = feasibleFinder.find(graph, from, to, constraints);
		if (feasiblePath == null) {
			return null;
		}

		LbpsaBnbFinder bnbFinder = new LbpsaBnbFinder(feasibleFinder,
				constraints);

		// The fact that this is the reverse search is accounted for
		// in the finder's implementation.
		Path resultPath = bnbFinder.find(graph, from, to);

		if (resultPath == null) {
			return null;
		}

		return resultPath;
	}
}
