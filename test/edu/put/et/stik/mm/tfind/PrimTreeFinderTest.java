package edu.put.et.stik.mm.tfind;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.put.et.stik.mm.helpers.TopologyAnalyser;
import edu.put.et.stik.mm.helpers.TopologyAnalyserImpl;
import edu.put.et.stik.mm.helpers.metrprov.IndexMetricProvider;
import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.model.topology.AdjacencyListFactory;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.GraphFactory;
import edu.put.et.stik.mm.model.topology.Tree;
import edu.put.et.stik.mm.tfind.SpanningTreeFinder;
import edu.put.et.stik.mm.tfind.prim.PrimTreeFinder;

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

		final TopologyAnalyser topologyAnalyser = new TopologyAnalyserImpl();

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
