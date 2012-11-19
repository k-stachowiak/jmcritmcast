package impossible.kpfnd;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import org.junit.Test;

public class JimenezMarzalKspTest {

	@Test
	public void testGetPath() {
		
		// Helpers.
		// --------
		MetricProvider metricProvider = new IndexMetricProvider(0);

		// Prepare model.
		// --------------
		List<Double> costs = Arrays.asList(new Double[] { 1.0, 2.0, 3.0 });
		GraphFactory graphFactory = new AdjacencyListFactory();
		Graph graph = graphFactory.createNSimplePaths(costs);
		Node first = graph.getNode(0);
		Node last = graph.getNode(graph.getNumNodes() - 1);

		// Instantiate SUT.
		// ----------------
		JimenezMarzalKsp ksp = new JimenezMarzalKsp(metricProvider);
		ksp.initialize(graph, first);

		// Exercise SUT.
		// -------------
		Path p0 = ksp.getPath(last, 0);
		Path p1 = ksp.getPath(last, 1);
		Path p2 = ksp.getPath(last, 2);
		
		System.out.println("p0 : " + p0.toString());
		System.out.println("p1 : " + p1.toString());
		System.out.println("p2 : " + p2.toString());

		// Perform the assertions.
		// -----------------------
		double expectedCost0 = costs.get(0);
		double expectedCost1 = costs.get(1);
		double expectedCost2 = costs.get(2);

		double actualCost0 = p0.getMetrics().get(0);
		double actualCost1 = p1.getMetrics().get(0);
		double actualCost2 = p2.getMetrics().get(0);

		assertEquals(expectedCost0, actualCost0, 0.1);
		assertEquals(expectedCost1, actualCost1, 0.1);
		assertEquals(expectedCost2, actualCost2, 0.1);
	}

}
