package impossible.helpers;

import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Tree;
import impossible.tfind.SpanningTreeFinder;

import java.util.ArrayList;
import java.util.List;


public class TopologyAnalyserImpl implements TopologyAnalyser {

	private final SpanningTreeFinder spanningTreeFinder;

	public TopologyAnalyserImpl(SpanningTreeFinder spanningTreeFinder) {
		this.spanningTreeFinder = spanningTreeFinder;
	}

	@Override
	public boolean isConnected(Graph graph) {

		if (graph.getNumNodes() == 0)
			return true;

		Tree spanningTree = spanningTreeFinder.find(graph.getNodes().get(0),
				graph);

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
