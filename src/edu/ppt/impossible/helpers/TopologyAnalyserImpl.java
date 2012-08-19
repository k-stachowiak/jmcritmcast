package edu.ppt.impossible.helpers;

import java.util.ArrayList;
import java.util.List;

import edu.ppt.impossible.model.Edge;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Tree;
import edu.ppt.impossible.tfind.SpanningTreeFinder;

public class TopologyAnalyserImpl implements TopologyAnalyser {

	private final SpanningTreeFinder spanningTreeFinder;

	public TopologyAnalyserImpl(SpanningTreeFinder spanningTreeFinder) {
		this.spanningTreeFinder = spanningTreeFinder;
	}

	@Override
	public boolean isConnected(Graph graph) {
		Tree spanningTree = spanningTreeFinder.find(graph);
		return graph.getNumNodes() == spanningTree.getNumNodes();
	}

	public List<Double> sumGraphMetrics(Graph graph) {

		List<Double> result = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics(); ++m) {
			result.add(0.0);
		}

		for (Edge edge : graph.getEdges()) {
			for (int m = 0; m < graph.getNumMetrics(); ++m) {
				result.set(m, result.get(m) + edge.getMetrics().get(m));
			}
		}

		return result;
	}
}
