package impossible.tfind.xamcra;

import java.util.List;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Edge;

public class XamcraMetricProvider extends MetricProvider {

	private final List<Double> constraints;

	public XamcraMetricProvider(List<Double> constraints) {
		this.constraints = constraints;
	}

	@Override
	public Double get(Edge edge) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < edge.getMetrics().size(); ++i) {
			double candidate = edge.getMetrics().get(i) / constraints.get(i);
			if (candidate > max) {
				max = candidate;
			}
		}
		return max;
	}

}
