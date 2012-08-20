package edu.ppt.impossible.helpers.gphmut;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.ppt.impossible.model.Edge;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.GraphFactory;
import edu.ppt.impossible.model.Node;

public class MetricRedistributionImpl implements MetricRedistribution {

	private final GraphFactory graphFactory;
	private final Random random;

	public MetricRedistributionImpl(GraphFactory graphFactory, Random random) {
		this.graphFactory = graphFactory;
		this.random = random;
	}

	@Override
	public Graph redistUniform(Graph graph,
			List<UniformDistributionParameters> parameters) {

		List<Node> nodes = graph.getNodes();
		List<Edge> edges = new ArrayList<>();

		for (Edge edge : graph.getEdges()) {
			List<Double> newMetrics = new ArrayList<>();
			for (UniformDistributionParameters dist : parameters) {
				double min = dist.getMin();
				double max = dist.getMax();
				double metric = random.nextDouble() * (max - min) + min;
				newMetrics.add(metric);
			}
			edges.add(new Edge(edge.getFrom(), edge.getTo(), newMetrics));
		}

		return graphFactory.createFromLists(nodes, edges);
	}

}
