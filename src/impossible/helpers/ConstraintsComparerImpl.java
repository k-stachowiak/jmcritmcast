package impossible.helpers;

import impossible.model.SubGraph;

import java.util.List;

public class ConstraintsComparerImpl implements ConstraintsComparer {

	@Override
	public boolean fulfilsAll(SubGraph subGraph, List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) > constraints.get(m - 1))
				return false;
		}
		return true;
	}

	@Override
	public boolean breaksAll(SubGraph subGraph, List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) <= constraints.get(m - 1))
				return false;
		}
		return true;
	}

	@Override
	public boolean fulfilsIndex(SubGraph subGraph, int m, double constraint) {
		return subGraph.getMetrics().get(m) <= constraint;
	}
}
