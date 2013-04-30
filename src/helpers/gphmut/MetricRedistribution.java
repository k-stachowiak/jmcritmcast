package helpers.gphmut;


import java.util.List;

import model.topology.Graph;



public interface MetricRedistribution {
	Graph redistUniform(Graph graph,
			List<UniformDistributionParameters> parameters);
}
