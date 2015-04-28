package helpers;

import helpers.metrprov.IndexMetricProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

import model.topology.Edge;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodePair;
import model.topology.SubGraph;
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

	public static SubGraph getNeighborhood(Graph graph, Node node) {

		HashSet<Integer> nodes = new HashSet<>();
		for (Node n : graph.getNeighbors(node)) {
			nodes.add(n.getId());
		}
		for (Node n : graph.getPredecessors(node)) {
			nodes.add(n.getId());
		}

		HashSet<EdgeDefinition> edges = new HashSet<>();
		for (int u : nodes) {
			for (int v : nodes) {
				if (u == v) {
					continue;
				}
				Edge edge = graph.getEdge(u, v);
				if (edge != null) {
					edges.add(new EdgeDefinition(u, v));
				}
			}
		}
		return new SubGraph(graph, new ArrayList<Integer>(nodes),
				new ArrayList<EdgeDefinition>(edges));
	}

	public static double averageDegree(Graph graph) {
		double sum = 0.0;
		int count = 0;
		for (Node node : graph.getNodes()) {
			
			HashSet<Integer> nodes = new HashSet<>();
			for (Node n : graph.getNeighbors(node))
				nodes.add(n.getId());
			for (Node n : graph.getPredecessors(node))
				nodes.add(n.getId());
			
			sum += nodes.size();
			count += 1;
		}
		
		return sum / count;
	}

	public static double diameter(Graph graph) {
		IndexMetricProvider mp = new IndexMetricProvider(0);
		FloydWarshallAllPathLengthFinder aplf = new FloydWarshallAllPathLengthFinder();
		Map<NodePair, Double> lengths = aplf.find(graph, mp);
		return Collections.max(lengths.values());
	}

	public static double clusteringCoefficient(Graph graph) {
		
		double sum = 0.0;
		int count = 0;
		for (Node node : graph.getNodes()) {
			
			HashSet<Integer> nodes = new HashSet<>();
			for (Node n : graph.getNeighbors(node))
				nodes.add(n.getId());
			for (Node n : graph.getPredecessors(node))
				nodes.add(n.getId());
			if (nodes.size() == 1)
				continue;
			
			SubGraph neighgorhood = getNeighborhood(graph, node);
			
			double numerator = 2 * neighgorhood.getNumEdges();
			double denominator = CombinatoricsUtils.binomialCoefficient(nodes.size(), 2);
			
			sum += numerator / denominator;
			count += 1;
		}
		
		return sum / count;
	}
}
