package impossible.helpers;

import impossible.model.topology.SubGraph;

import java.util.List;

public class ConstraintsComparerImpl implements ConstraintsComparer {

	/* (non-Javadoc)
	 * @see impossible.helpers.ConstraintsComparer#fulfilsAll(impossible.model.topology.SubGraph, java.util.List)
	 */
	@Override
	public boolean fulfilsAll(SubGraph subGraph, List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) > constraints.get(m - 1))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see impossible.helpers.ConstraintsComparer#breaksAll(impossible.model.topology.SubGraph, java.util.List)
	 */
	@Override
	public boolean breaksAll(SubGraph subGraph, List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) <= constraints.get(m - 1))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see impossible.helpers.ConstraintsComparer#fulfilsIndex(impossible.model.topology.SubGraph, int, double)
	 */
	@Override
	public boolean fulfilsIndex(SubGraph subGraph, int m, double constraint) {
		return subGraph.getMetrics().get(m) <= constraint;
	}
}
