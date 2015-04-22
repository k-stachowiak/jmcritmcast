package tfind;

import static org.junit.Assert.assertEquals;
import helpers.TopologyAnalyser;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Tree;

import org.junit.Test;

import tfind.SpanningTreeFinder;
import tfind.prim.PrimTreeFinder;


public class PrimTreeFinderTest {

	@Test
	public void testForDistonnectedGraphFind() {
		// Constants.
		final int INDEX_FOR_METRIC_PROVIDER = 0;

		// Helpers.
		final GraphFactory graphFactory = new AdjacencyListFactory();

		final MetricProvider metricProvider = new IndexMetricProvider(
				INDEX_FOR_METRIC_PROVIDER);

		final SpanningTreeFinder spanningTreeFinder = new PrimTreeFinder(
				metricProvider);

		// Input.
		Graph graph = graphFactory.createDisconnected();

		// Case.
		Tree tree = spanningTreeFinder.find(graph.getNode(0), graph);

		int expectedNumNodes = 3;
		int expectedNumEdges = 2;

		int actualNumNodes = tree.getNumNodes();
		int actualNumEdges = tree.getNumEdges();

		// Assert.
		assertEquals(expectedNumNodes, actualNumNodes);
		assertEquals(expectedNumEdges, actualNumEdges);
	}

	@Test
	public void testForCutsCheapestEdgesNPE() {

		// Constants.
		final int INDEX_FOR_METRIC_PROVIDER = 0;

		// Helpers.
		final GraphFactory graphFactory = new AdjacencyListFactory();

		final MetricProvider metricProvider = new IndexMetricProvider(
				INDEX_FOR_METRIC_PROVIDER);

		final TopologyAnalyser topologyAnalyser = new TopologyAnalyser();

		// Input.
		Graph connectedGraph = graphFactory.createMaciejPiechowiakExample();

		// Case.
		boolean expected = true;
		boolean actual = topologyAnalyser.isConnected(connectedGraph,
				new PrimTreeFinder(metricProvider));

		// Assert.
		assertEquals(expected, actual);
	}

}
