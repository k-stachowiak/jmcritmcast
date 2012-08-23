package impossible.pfnd.lbpsa;

import java.util.ArrayList;
import java.util.List;

import impossible.helpers.ConstraintsComparer;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;

public class LbpsaPathFinder implements ConstrainedPathFinder {

	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	private List<Double> constraints;

	public LbpsaPathFinder(PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer, List<Double> constraints) {
		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;
		this.constraints = constraints;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		LbpsaFeasibleFinder feasibleFinder = new LbpsaFeasibleFinder(
				pathFinderFactory, constraintsComparer, constraints);

		if (feasibleFinder.find(graph, from, to) == null)
			return null;

		LbpsaBnbFinder bnbFinder = new LbpsaBnbFinder(feasibleFinder);
		return bnbFinder.find(graph, from, to);
	}

	@Override
	public void setConstraints(List<Double> constraints) {
		this.constraints = new ArrayList<>(constraints);
	}

}
