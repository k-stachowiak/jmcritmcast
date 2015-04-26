package helpers;

import helpers.metrprov.IndexMetricProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodePair;
import model.topology.Tree;
import tfind.SpanningTreeFinder;
import aplfnd.FloydWarshallAllPathLengthFinder;

public class TopologyAnalyser {

	public static boolean isConnected(Graph graph,
			SpanningTreeFinder spanningTreeFinder) {

		if (graph.getNumNodes() == 0)
			return true;

		Tree spanningTree = spanningTreeFinder.find(graph.getNodes().get(0),
				graph);

		return graph.getNumNodes() == spanningTree.getNumNodes();
	}

	public static List<Double> sumGraphMetrics(Graph graph) {

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

	public static boolean equal(Graph a, Graph b) {
		Set<Node> aNodes = new HashSet<>(a.getNodes());
		Set<Node> bNodes = new HashSet<>(b.getNodes());
		if (!aNodes.equals(bNodes))
			return false;

		Set<Edge> aEdges = new HashSet<>(a.getEdges());
		Set<Edge> bEdges = new HashSet<>(b.getEdges());
		if (!aEdges.equals(bEdges))
			return false;

		return true;
	}

	public static double averageDegree(Graph graph) {
		int sum = 0;
		List<Node> nodes = graph.getNodes();
		for (Node node : nodes) {
			sum += graph.getNeighbors(node).size();
		}
		return (double) sum / (double) nodes.size();
	}

	public static double diameter(Graph graph) {
		IndexMetricProvider mp = new IndexMetricProvider(0);
		FloydWarshallAllPathLengthFinder aplf = new FloydWarshallAllPathLengthFinder();
		Map<NodePair, Double> lengths = aplf.find(graph, mp);
		return Collections.min(lengths.values());
	}

	public static double clusteringCoefficient(Graph graph) {
		return -1;
	}
}
