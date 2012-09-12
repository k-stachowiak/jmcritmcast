package impossible.pfnd.lbpsa;

import impossible.helpers.ConstraintsComparer;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
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

		if (feasibleFinder.find(graph, from, to, constraints) == null)
			return null;

		LbpsaBnbFinder bnbFinder = new LbpsaBnbFinder(feasibleFinder, constraints);
		return bnbFinder.find(graph, from, to);
	}

}
