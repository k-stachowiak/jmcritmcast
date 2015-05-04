package aplfnd;

import helpers.PathMetric;
import helpers.metrprov.MetricProvider;

import java.util.Map;

import model.topology.Graph;
import model.topology.NodePair;

public interface AllPathLengthFinder {
	Map<NodePair, PathMetric> find(Graph graph, MetricProvider metricProvider);
}
