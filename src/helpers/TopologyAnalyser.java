package helpers;

import helpers.metrprov.HopMetricProvider;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

import pfnd.MetricRelaxation;
import pfnd.PathFinder;
import pfnd.dkstr.DijkstraPathFinder;
import model.topology.Edge;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodePair;
import model.topology.Path;
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
		return averageDegree(graph, graph.getNodes());
	}

	public static double averageDegree(Graph graph, List<Node> nodes) {
		double sum = 0.0;
		int count = 0;
		for (Node node : nodes) {

			HashSet<Integer> neighbors = new HashSet<>();
			for (Node n : graph.getNeighbors(node))
				neighbors.add(n.getId());
			for (Node n : graph.getPredecessors(node))
				neighbors.add(n.getId());

			sum += neighbors.size();
			count += 1;
		}

		return sum / count;
	}

	public static PathMetric diameter(Graph graph) {

		FloydWarshallAllPathLengthFinder aplf = new FloydWarshallAllPathLengthFinder();
		Map<NodePair, PathMetric> lengths = aplf.find(graph,
				new IndexMetricProvider(0));

		double maxHops = Collections.max(lengths.values(),
				new PathMetric.HopComparer()).getHop();

		double maxCost = Collections.max(lengths.values(),
				new PathMetric.CostComparer()).getCost();

		return new PathMetric(maxHops, maxCost);
	}

	public static PathMetric diameter(Graph graph, List<Node> nodes) {

		MetricProvider imp = new IndexMetricProvider(0);
		MetricProvider hmp = new HopMetricProvider();

		PathFinder costPathFinder = new DijkstraPathFinder(
				new MetricRelaxation(imp));
		PathFinder hopPathFinder = new DijkstraPathFinder(new MetricRelaxation(
				hmp));

		ArrayList<Path> costPaths = new ArrayList<>();
		ArrayList<Path> hopPaths = new ArrayList<>();
		for (int i = 0; i < nodes.size(); ++i) {
			for (int j = i + 1; j < nodes.size(); ++j) {
				costPaths.add(costPathFinder.find(graph, nodes.get(i),
						nodes.get(j)));
				hopPaths.add(hopPathFinder.find(graph, nodes.get(i),
						nodes.get(j)));
			}
		}

		double maxCost = Double.NEGATIVE_INFINITY;
		double maxHops = Double.NEGATIVE_INFINITY;
		for (Path p : costPaths) {

			double cost = imp.getPostAdditive(p);
			if (cost > maxCost) {
				maxCost = cost;
			}

			double hops = hmp.getPostAdditive(p);
			if (hops > maxHops) {
				maxHops = hops;
			}
		}

		return new PathMetric(maxHops, maxCost);
	}

	public static double clusteringCoefficient(Graph graph) {
		return clusteringCoefficient(graph, graph.getNodes());
	}

	public static double clusteringCoefficient(Graph graph, List<Node> nodes) {

		double sum = 0.0;
		int count = 0;
		for (Node node : nodes) {

			HashSet<Integer> neighbors = new HashSet<>();
			for (Node n : graph.getNeighbors(node))
				neighbors.add(n.getId());
			for (Node n : graph.getPredecessors(node))
				neighbors.add(n.getId());
			if (neighbors.size() == 1)
				continue;

			SubGraph neighgorhood = getNeighborhood(graph, node);

			double numerator = 2 * neighgorhood.getNumEdges();
			double denominator = CombinatoricsUtils.binomialCoefficient(
					neighbors.size(), 2);

			sum += numerator / denominator;
			count += 1;
		}

		return sum / count;
	}

	public static void minMaxCoordinates(Graph graph, Double minX, Double maxX,
			Double minY, Double maxY) {

		minX = Double.POSITIVE_INFINITY;
		maxX = Double.NEGATIVE_INFINITY;
		minY = Double.POSITIVE_INFINITY;
		maxY = Double.NEGATIVE_INFINITY;

		for (Node n : graph.getNodes()) {
			if (n.getX() < minX) {
				minX = n.getX();
			}
			if (n.getX() > maxX) {
				maxX = n.getX();
			}
			if (n.getY() < minY) {
				minY = n.getY();
			}
			if (n.getY() > maxY) {
				maxY = n.getY();
			}
		}
	}

	public static double nodeGroupoDensity(Graph graph, List<Node> group) {
		return (double) group.size() / (double) graph.getNumNodes();
	}
}
