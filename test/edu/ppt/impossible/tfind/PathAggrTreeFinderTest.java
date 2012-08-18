package edu.ppt.impossible.tfind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import edu.ppt.impossible.helpers.FengGroupConstraintsChooser;
import edu.ppt.impossible.helpers.GroupConstraintsChooser;
import edu.ppt.impossible.helpers.IndexMetricProvider;
import edu.ppt.impossible.helpers.MetricProvider;
import edu.ppt.impossible.helpers.NodeGroupper;
import edu.ppt.impossible.helpers.RandomNodeGroupper;
import edu.ppt.impossible.model.AdjacencyListFactory;
import edu.ppt.impossible.model.Edge;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.GraphFactory;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.SubGraph;
import edu.ppt.impossible.model.Tree;
import edu.ppt.impossible.pfnd.PathFinder;
import edu.ppt.impossible.pfnd.PathFinderFactory;
import edu.ppt.impossible.pfnd.PathFinderFactoryImpl;

public class PathAggrTreeFinderTest {

	@Test
	public void testFindTestGraph() {

		Random random = new Random(0);

		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		MetricProvider metricProvider = new IndexMetricProvider(0);
		final NodeGroupper nodeGroupper = new RandomNodeGroupper(random);
		GroupConstraintsChooser constraintsChooser = new FengGroupConstraintsChooser(
				0.9, pathFinderFactory);

		PathFinder pathFinder = pathFinderFactory.CreateDijkstraIndex(0);
		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		Graph graph = graphFactory.createTest();

		List<Node> group = nodeGroupper.group(graph, 3);
		List<Double> constraints = constraintsChooser.choose(graph, group);

		SteinerTreeFinder treeFinder = treeFinderFactory.createPathAggr(
				constraints, pathFinder, spanningTreeFinder);

		Tree tree = treeFinder.find(graph, group);

		assertNotNull(tree);
	}

	@Test
	public void testFind() {

		// Constants.
		// ----------
		double CHEAP_METRIC = 10.0;
		double EXPENSIVE_METRIC = 1000.0;

		// Helpers.
		// --------

		// Strategies
		MetricProvider metricProvider = new IndexMetricProvider(0);

		// Factories.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Finders.
		PathFinder pathFinder = pathFinderFactory.CreateDijkstraIndex(0);

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

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

		List<Double> constraints = new ArrayList<>();
		constraints.add(4 * CHEAP_METRIC);

		Graph graph = graphFactory.createFromLists(nodes, edges);

		List<Node> group = new ArrayList<>();
		group.add(graph.getNode(0));
		group.add(graph.getNode(7));
		group.add(graph.getNode(8));

		List<SubGraph.EdgeDefinition> expectedEdges = new ArrayList<>();
		expectedEdges.add(new SubGraph.EdgeDefinition(0, 1));
		expectedEdges.add(new SubGraph.EdgeDefinition(1, 3));
		expectedEdges.add(new SubGraph.EdgeDefinition(3, 7));
		expectedEdges.add(new SubGraph.EdgeDefinition(0, 2));
		expectedEdges.add(new SubGraph.EdgeDefinition(2, 6));
		expectedEdges.add(new SubGraph.EdgeDefinition(6, 8));

		Tree expectedTree = new Tree(graph, expectedEdges);

		// Case.
		// -----
		SteinerTreeFinder steinerTreeFinder = treeFinderFactory.createPathAggr(
				constraints, pathFinder, spanningTreeFinder);

		Tree actualTree = steinerTreeFinder.find(graph, group);

		// Assert.
		// -------
		assertEquals(expectedTree, actualTree);
	}
}
