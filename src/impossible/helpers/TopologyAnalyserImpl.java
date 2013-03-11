package impossible.helpers;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.SpanningTreeFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TopologyAnalyserImpl implements TopologyAnalyser {

	@Override
	public boolean isConnected(Graph graph, SpanningTreeFinder spanningTreeFinder) {

		if (graph.getNumNodes() == 0)
			return true;

		Tree spanningTree = spanningTreeFinder.find(graph.getNodes().get(0),
				graph);

		return graph.getNumNodes() == spanningTree.getNumNodes();
	}

	@Override
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
	
	@Override
	public boolean equal(Graph a, Graph b) {		
		Set<Node> aNodes = new HashSet<>(a.getNodes());
		Set<Node> bNodes = new HashSet<>(b.getNodes());
		if(!aNodes.equals(bNodes)) return false;
		
		Set<Edge> aEdges = new HashSet<>(a.getEdges());
		Set<Edge> bEdges = new HashSet<>(b.getEdges());		
		if(!aEdges.equals(bEdges)) return false;
		
		return true;
	}
}
