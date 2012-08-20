package edu.ppt.impossible.helpers.gphmut;

import java.util.List;

import edu.ppt.impossible.model.Graph;

public interface MetricRedistribution {
	Graph redistUniform(Graph graph,
			List<UniformDistributionParameters> parameters);
}
