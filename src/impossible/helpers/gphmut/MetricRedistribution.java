package impossible.helpers.gphmut;

import impossible.model.topology.Graph;

import java.util.List;


public interface MetricRedistribution {
	Graph redistUniform(Graph graph,
			List<UniformDistributionParameters> parameters);
}
