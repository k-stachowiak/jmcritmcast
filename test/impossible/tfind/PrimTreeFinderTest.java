package impossible.tfind;

import static org.junit.Assert.assertEquals;
import impossible.helpers.TopologyAnalyser;
import impossible.helpers.TopologyAnalyserImpl;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.AdjacencyListFactory;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Tree;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.prim.PrimTreeFinder;

import org.junit.Test;


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

		final TopologyAnalyser topologyAnalyser = new TopologyAnalyserImpl(
				new PrimTreeFinder(metricProvider));

		// Input.
		Graph graph = graphFactory.createTest();

		// Case.
		boolean expected = true;
		boolean actual = topologyAnalyser.isConnected(graph);

		// Assert.
		assertEquals(expected, actual);
	}

}
