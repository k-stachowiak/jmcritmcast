package impossible.helpers.metrprov;

import java.util.List;

import impossible.model.topology.Edge;

public class LagrangeMetricProvider extends MetricProvider {

	private final int offset;
	private final List<Double> constraints;
	private final List<Double> lambdas;

	public LagrangeMetricProvider(int offset, List<Double> constraints,
			List<Double> lambdas) {
		this.offset = offset;
		this.constraints = constraints;
		this.lambdas = lambdas;
	}

	@Override
	public Double get(Edge edge) {

		double result = 0;
		for (int i = 0; i < offset; ++i)
			result += edge.getMetrics().get(i);

		for (int i = offset; i < edge.getMetrics().size(); ++i)
			result += (edge.getMetrics().get(i) - constraints.get(i - offset))
					* lambdas.get(i - offset);

		return result;
	}

}
