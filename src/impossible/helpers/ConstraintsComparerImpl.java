package impossible.helpers;

import java.util.List;

import impossible.model.SubGraph;

public class ConstraintsComparerImpl implements ConstraintsComparer {

	public boolean fulfilsConstraints(SubGraph subGraph,
			List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) > constraints.get(m - 1))
				return false;
		}
		return true;
	}

}
