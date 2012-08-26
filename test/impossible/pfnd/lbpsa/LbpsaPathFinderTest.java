package impossible.pfnd.lbpsa;

import static org.junit.Assert.assertEquals;
import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.model.AdjacencyListFactory;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LbpsaPathFinderTest {

	@Test
	public final void testFind() {
		// Factories.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();

		// Strategies.
		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();

		// Model.
		Graph graph = graphFactory.createMaciejPiechowiakExample();

		// Assumptions.
		Node from = graph.getNode(0);
		Node to = graph.getNode(7);

		List<Double> constraints = new ArrayList<>();
		constraints.add(1000.0);

		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(6);
		expectedNodes.add(4);
		expectedNodes.add(7);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		PathFinder pathFinder = pathFinderFactory.createLbpsa(
				constraintsComparer, constraints);

		// Exercise SUT.
		Path actualPath = pathFinder.find(graph, from, to);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

}
