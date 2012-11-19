package impossible.tfind.generalprim;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Edge;

import java.util.List;

public class CheapestEdgeSelector implements EdgeSelector {
	
	private final MetricProvider metricProvider;

	public CheapestEdgeSelector(MetricProvider metricProvider) {
		this.metricProvider = metricProvider;
	}

	@Override
	public void reset() {
		// This one is, fortunately, stateless.
	}

	@Override
	public Edge select(List<Edge> cutEdges) {
		Edge candidate = null;
		double cheapest = Double.POSITIVE_INFINITY;
		for (Edge edge : cutEdges) {
			double cost = metricProvider.get(edge);
			if (candidate == null || cost < cheapest) {
				candidate = edge;
				cheapest = cost;
			}
		}
		return candidate;
	}
	
}
