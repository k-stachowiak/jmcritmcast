package edu.put.et.stik.mm.helpers.gphmut;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;


public interface MetricRedistribution {
	Graph redistUniform(Graph graph,
			List<UniformDistributionParameters> parameters);
}
