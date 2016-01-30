package helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.CombinatoricsUtils;

import aplfnd.FloydWarshallAllPathLengthFinder;
import helpers.metrprov.HopMetricProvider;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;
import model.topology.Edge;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodePair;
import model.topology.Path;
import model.topology.SubGraph;
import model.topology.Tree;
import pfnd.MetricRelaxation;
import pfnd.PathFinder;
import pfnd.dkstr.DijkstraPathFinder;
import tfind.SpanningTreeFinder;

public class TopologyAnalyser {

	public static class MinMaxSquare {
		private final double minX;
		private final double maxX;
		private final double minY;
		private final double maxY;

		public MinMaxSquare(double minX, double maxX, double minY, double maxY) {
			super();
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}

		public double getMinX() {
			return minX;
		}

		public double getMaxX() {
			return maxX;
		}

		public double getMinY() {
			return minY;
		}

		public double getMaxY() {
			return maxY;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(maxX);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(maxY);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(minX);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(minY);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MinMaxSquare other = (MinMaxSquare) obj;
			if (Double.doubleToLongBits(maxX) != Double.doubleToLongBits(other.maxX))
				return false;
			if (Double.doubleToLongBits(maxY) != Double.doubleToLongBits(other.maxY))
				return false;
			if (Double.doubleToLongBits(minX) != Double.doubleToLongBits(other.minX))
				return false;
			if (Double.doubleToLongBits(minY) != Double.doubleToLongBits(other.minY))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MinMaxSquare [minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY + "]";
		}

	}

	public static boolean isConnected(Graph graph, SpanningTreeFinder spanningTreeFinder) {

		if (graph.getNumNodes() == 0)
			return true;

		Tree spanningTree = spanningTreeFinder.find(graph.getNodes().get(0), graph);

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
		return new SubGraph(graph, new ArrayList<Integer>(nodes), new ArrayList<EdgeDefinition>(edges));
	}

	public static StatisticalSummary degreeStatistics(Graph graph) {
		SummaryStatistics result = new SummaryStatistics();
		for (Node node : graph.getNodes()) {

			HashSet<Integer> neighbors = new HashSet<>();
			for (Node n : graph.getNeighbors(node))
				neighbors.add(n.getId());
			for (Node n : graph.getPredecessors(node))
				neighbors.add(n.getId());

			result.addValue(neighbors.size());
		}
		return result;
	}

	public static List<StatisticalSummary> metricStatistics(SubGraph subgraph) {
		
		List<Double>
		
		List<Edge> edges = subgraph.getEdges();
		int numMetrics = edges.get(0).getMetrics().size();
		ArrayList<SummaryStatistics> resultConcrete = new ArrayList<>();
		for (int i = 0; i < numMetrics; ++i) {
			resultConcrete.add(new SummaryStatistics());
		}
		for (Edge edge : edges) {
			for (int i = 0; i < numMetrics; ++i) {
				resultConcrete.get(i).addValue(edge.getMetrics().get(i));
			}
		}
		List<StatisticalSummary> result = new ArrayList<>();
		for (SummaryStatistics ss : resultConcrete) {
			result.add(ss);
		}
		return result;
	}

	public static List<StatisticalSummary> metricStatistics(List<SubGraph> subgraphs) {
		int numMetrics = subgraphs.get(0).getMetrics().size();
		ArrayList<SummaryStatistics> resultConcrete = new ArrayList<>();
		for (int i = 0; i < numMetrics; ++i) {
			resultConcrete.add(new SummaryStatistics());
		}
		for (SubGraph sg : subgraphs) {
			List<Double> metrics = sg.getMetrics();
			for (int i = 0; i < metrics.size(); ++i) {
				resultConcrete.get(i).addValue(metrics.get(i));
			}
		}
		List<StatisticalSummary> result = new ArrayList<>();
		for (SummaryStatistics ss : resultConcrete) {
			result.add(ss);
		}
		return result;
	}

	public static PathMetric diameter(Graph graph) {

		FloydWarshallAllPathLengthFinder aplf = new FloydWarshallAllPathLengthFinder();
		Map<NodePair, PathMetric> lengths = aplf.find(graph, new IndexMetricProvider(0));

		double maxHops = Collections.max(lengths.values(), new PathMetric.HopComparer()).getHop();

		double maxCost = Collections.max(lengths.values(), new PathMetric.CostComparer()).getCost();

		return new PathMetric(maxHops, maxCost);
	}

	public static PathMetric diameter(Graph graph, List<Node> nodes) {

		MetricProvider imp = new IndexMetricProvider(0);
		MetricProvider hmp = new HopMetricProvider();

		PathFinder costPathFinder = new DijkstraPathFinder(new MetricRelaxation(imp));
		PathFinder hopPathFinder = new DijkstraPathFinder(new MetricRelaxation(hmp));

		ArrayList<Path> costPaths = new ArrayList<>();
		ArrayList<Path> hopPaths = new ArrayList<>();
		for (int i = 0; i < nodes.size(); ++i) {
			for (int j = i + 1; j < nodes.size(); ++j) {
				costPaths.add(costPathFinder.find(graph, nodes.get(i), nodes.get(j)));
				hopPaths.add(hopPathFinder.find(graph, nodes.get(i), nodes.get(j)));
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
			double denominator = CombinatoricsUtils.binomialCoefficient(neighbors.size(), 2);

			sum += numerator / denominator;
			count += 1;
		}

		return sum / count;
	}

	public static MinMaxSquare minMaxCoordinates(Graph graph) {

		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

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

		return new MinMaxSquare(minX, maxX, minY, maxY);
	}

	public static double nodeGroupoDensity(Graph graph, List<Node> group) {
		return (double) group.size() / (double) graph.getNumNodes();
	}
}
