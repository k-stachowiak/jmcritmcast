package edu.put.et.stik.mm.pfnd.lbpsa;


import java.util.List;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;

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
