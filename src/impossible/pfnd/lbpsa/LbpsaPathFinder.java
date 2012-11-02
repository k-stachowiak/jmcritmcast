package impossible.pfnd.lbpsa;

import impossible.helpers.ConstraintsComparer;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;

import java.util.List;

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
