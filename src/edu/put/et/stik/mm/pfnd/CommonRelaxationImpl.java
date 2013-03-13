package edu.put.et.stik.mm.pfnd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.model.topology.Edge;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.util.NodeComparator;


public class CommonRelaxationImpl extends CommonRelaxation {

	private final MetricProvider metricProvider;

	Map<Node, List<Double>> labels;
	Map<Node, Double> aggregatedLabels;

	public CommonRelaxationImpl(MetricProvider metricProvider) {
		this.metricProvider = metricProvider;
	}

	@Override
	public void reset(Graph graph, Node from) {

		labels = new TreeMap<>(new NodeComparator());
		aggregatedLabels = new TreeMap<>(new NodeComparator());
		predecessors = new TreeMap<>(new NodeComparator());

		for (Node node : graph.getNodes()) {

			double labelValue = (node.equals(from)) ? 0
					: Double.POSITIVE_INFINITY;

			List<Double> label = new ArrayList<>();
			for (int m = 0; m < graph.getNumMetrics(); ++m) {
				label.add(labelValue);
			}

			labels.put(node, label);
			aggregatedLabels.put(node, labelValue);
			predecessors.put(node, node);
		}
	}

	@Override
	public boolean relax(Graph graph, Node from, Node to) {

		Edge edge = graph.getEdge(from.getId(), to.getId());

		double candidateDistance = aggregatedLabels.get(from)
				+ metricProvider.get(edge);

		if (candidateDistance < aggregatedLabels.get(to)) {

			List<Double> label = new ArrayList<>();
			for (int m = 0; m < graph.getNumMetrics(); ++m) {
				label.add(labels.get(from).get(m) + edge.getMetrics().get(m));
			}

			labels.put(to, label);
			aggregatedLabels.put(to, candidateDistance);
			predecessors.put(to, from);
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isCheaper(Node a, Node b) {
		return aggregatedLabels.get(a) < aggregatedLabels.get(b);
	}

	public Map<Node, List<Double>> getLabels() {
		return labels;
	}

}
