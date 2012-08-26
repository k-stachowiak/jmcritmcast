package impossible.helpers.metrprov;

import impossible.model.Edge;

import java.util.List;

public class LinearCombinationMetricProvider extends MetricProvider {

	private final int offset;
	private final List<Double> lambdas;

	public LinearCombinationMetricProvider(int offset, List<Double> lambdas) {

		this.offset = offset;
		this.lambdas = lambdas;
	}

	@Override
	public Double get(Edge edge) {

		double result = 0;

		for (int i = 0; i < offset; ++i)
			result += edge.getMetrics().get(i);

		for (int i = offset; i < edge.getMetrics().size(); ++i)
			result += edge.getMetrics().get(i) * lambdas.get(i - offset);

		return result;
	}
}
