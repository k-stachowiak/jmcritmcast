package helpers.metrprov;

import model.topology.Edge;

public class IndexMetricProvider extends MetricProvider {

	private final int index;

	public IndexMetricProvider(int index) {
		this.index = index;
	}

	@Override
	public Double get(Edge edge) {
		return edge.getMetrics().get(index);
	}

}
