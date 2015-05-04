package helpers.metrprov;

import model.topology.Edge;

public class HopMetricProvider extends MetricProvider {

	@Override
	public Double get(Edge edge) {
		return 1.0;
	}

}
