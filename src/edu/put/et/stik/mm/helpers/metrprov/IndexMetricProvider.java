package edu.put.et.stik.mm.helpers.metrprov;

import edu.put.et.stik.mm.model.topology.Edge;

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
