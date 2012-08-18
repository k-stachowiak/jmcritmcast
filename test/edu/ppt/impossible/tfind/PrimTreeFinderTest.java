package edu.ppt.impossible.tfind;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ppt.impossible.helpers.TopologyAnalyser;
import edu.ppt.impossible.helpers.TopologyAnalyserImpl;
import edu.ppt.impossible.helpers.metrprov.IndexMetricProvider;
import edu.ppt.impossible.helpers.metrprov.MetricProvider;
import edu.ppt.impossible.model.AdjacencyListFactory;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.GraphFactory;

public class PrimTreeFinderTest {

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
