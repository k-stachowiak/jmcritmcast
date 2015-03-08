package tfind;

import static org.junit.Assert.assertEquals;

import helpers.PathAggregator;
import helpers.PathAggregatorImpl;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;

import java.util.ArrayList;
import java.util.List;

import model.topology.AdjacencyListFactory;
import model.topology.Edge;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Tree;

import org.junit.Test;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;

import tfind.ConstrainedSteinerTreeFinder;
import tfind.SpanningTreeFinder;
import tfind.TreeFinderFactory;
import tfind.TreeFinderFactoryImpl;


public class PathAggrTreeFinderTest {

	@Test
	public void testFind() {

		// Constants.
		// ----------
		double CHEAP_METRIC = 10.0;
		double EXPENSIVE_METRIC = 1000.0;

		List<Double> constraints = new ArrayList<>();
		constraints.add(4 * CHEAP_METRIC);

		// Helpers.
		// --------

		// Strategies
		MetricProvider metricProvider = new IndexMetricProvider(0);

		// Factories.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Finders.
		ConstrainedPathFinder pathFinder = pathFinderFactory
				.createHmcp();

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		// Other strategies.
		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		// Model.
		// ------
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0, 0));
		nodes.add(new Node(1, 1, 2));
		nodes.add(new Node(2, 1, -2));
		nodes.add(new Node(3, 2, 3));
		nodes.add(new Node(4, 2, 1));
		nodes.add(new Node(5, 2, -1));
		nodes.add(new Node(6, 2, -3));
		nodes.add(new Node(7, 3, 2));
		nodes.add(new Node(8, 3, -2));

		List<Double> cheapMetrics = new ArrayList<>();
		cheapMetrics.add(CHEAP_METRIC);
		cheapMetrics.add(CHEAP_METRIC);

		List<Double> expensiveMetrics = new ArrayList<>();
		expensiveMetrics.add(EXPENSIVE_METRIC);
		expensiveMetrics.add(EXPENSIVE_METRIC);

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, cheapMetrics));
		edges.add(new Edge(0, 2, cheapMetrics));
		edges.add(new Edge(1, 3, cheapMetrics));
		edges.add(new Edge(1, 4, expensiveMetrics));
		edges.add(new Edge(2, 5, expensiveMetrics));
		edges.add(new Edge(2, 6, cheapMetrics));
		edges.add(new Edge(3, 7, cheapMetrics));
		edges.add(new Edge(4, 7, cheapMetrics));
		edges.add(new Edge(5, 8, cheapMetrics));
		edges.add(new Edge(6, 8, cheapMetrics));
		edges.add(new Edge(3, 4, expensiveMetrics));
		edges.add(new Edge(5, 6, expensiveMetrics));
		edges.add(new Edge(4, 8, expensiveMetrics));
		edges.add(new Edge(5, 7, expensiveMetrics));

		Graph graph = graphFactory.createFromLists(nodes, edges);

		List<Node> group = new ArrayList<>();
		group.add(graph.getNode(0));
		group.add(graph.getNode(7));
		group.add(graph.getNode(8));

		List<EdgeDefinition> expectedEdges = new ArrayList<>();
		expectedEdges.add(new EdgeDefinition(0, 1));
		expectedEdges.add(new EdgeDefinition(1, 3));
		expectedEdges.add(new EdgeDefinition(3, 7));
		expectedEdges.add(new EdgeDefinition(0, 2));
		expectedEdges.add(new EdgeDefinition(2, 6));
		expectedEdges.add(new EdgeDefinition(6, 8));

		Tree expectedTree = new Tree(graph, expectedEdges);

		// Case.
		// -----
		ConstrainedSteinerTreeFinder steinerTreeFinder = treeFinderFactory
				.createConstrainedPathAggr(pathFinder, pathAggregator);

		Tree actualTree = steinerTreeFinder.find(graph, group, constraints);

		// Assert.
		// -------
		assertEquals(expectedTree, actualTree);
	}
}
