package impossible.kpfnd;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import org.junit.Test;

public class JimenezMarzalKspTest {

	@Test
	public void testGetPath() {

		// Prepare model.
		// --------------
		List<Double> costs = Arrays.asList(new Double[] { 1.0, 2.0, 3.0 });
		GraphFactory graphFactory = new AdjacencyListFactory();
		Graph graph = graphFactory.createNSimplePaths(costs);
		Node first = graph.getNode(0);
		Node last = graph.getNode(graph.getNumNodes() - 1);

		// Instantiate SUT.
		// ----------------
		JimenezMarzalKsp ksp = new JimenezMarzalKsp();
		ksp.initialize(graph, first);

		// Exercise SUT.
		// -------------
		Path p0 = ksp.getPath(0, last);
		Path p1 = ksp.getPath(1, last);
		Path p2 = ksp.getPath(2, last);

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
