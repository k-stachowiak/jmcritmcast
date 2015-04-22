package pfnd.lbpsa;

import static org.junit.Assert.assertEquals;

import helpers.ConstraintsComparer;

import java.util.ArrayList;
import java.util.List;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Path;

import org.junit.BeforeClass;
import org.junit.Test;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;
import pfnd.lbpsa.LbpsaFeasibleFinder;


public class LbpsaPathFinderTest {

	private static GraphFactory graphFactory;
	private static PathFinderFactory pathFinderFactory;
	private static ConstraintsComparer constraintsComparer;

	@BeforeClass
	public static void beforeClass() {
		graphFactory = new AdjacencyListFactory();
		pathFinderFactory = new PathFinderFactoryImpl();
		constraintsComparer = new ConstraintsComparer();
	}

	/*
	 * @Test public final void testFind() { // Factories. GraphFactory
	 * graphFactory = new AdjacencyListFactory(); PathFinderFactory
	 * pathFinderFactory = new PathFinderFactoryImpl();
	 * 
	 * // Strategies. ConstraintsComparer constraintsComparer = new
	 * ConstraintsComparerImpl();
	 * 
	 * // Model. Graph graph = graphFactory.createMaciejPiechowiakExample();
	 * 
	 * // Assumptions. Node from = graph.getNode(0); Node to = graph.getNode(7);
	 * 
	 * List<Double> constraints = new ArrayList<>(); constraints.add(1000.0);
	 * 
	 * List<Integer> expectedNodes = new ArrayList<>(); expectedNodes.add(0);
	 * expectedNodes.add(6); expectedNodes.add(4); expectedNodes.add(7);
	 * 
	 * Path expectedPath = new Path(graph, expectedNodes);
	 * 
	 * // Instantiate SUT. PathFinder pathFinder =
	 * pathFinderFactory.createLbpsa( constraintsComparer, constraints);
	 * 
	 * // Exercise SUT. Path actualPath = pathFinder.find(graph, from, to);
	 * 
	 * // Assertions. assertEquals(expectedPath, actualPath); }
	 */

	@Test
	public void testFeasibleBig2MetrGraph() {

		Graph graph = graphFactory.createBig2Metr();

		List<Double> constraints = new ArrayList<>();
		constraints.add(40.0);
		constraints.add(100.0);

		Node from = graph.getNode(7);
		Node to = graph.getNode(2);

		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(7);
		expectedNodes.add(4);
		expectedNodes.add(6);
		expectedNodes.add(8);
		expectedNodes.add(9);
		expectedNodes.add(10);
		expectedNodes.add(11);
		expectedNodes.add(3);
		expectedNodes.add(5);
		expectedNodes.add(2);

		Path expectedPath = new Path(graph, expectedNodes);

		ConstrainedPathFinder pathFinder = pathFinderFactory.createLbpsa(
				constraintsComparer);

		Path actualPath = pathFinder.find(graph, from, to, constraints);

		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testFeasibleFindInfLoopGraph() {

		// Assumptions.
		Graph graph = graphFactory.createInfLoop();
		Node from = graph.getNode(0);
		Node to = graph.getNode(6);

		List<Double> constraints = new ArrayList<>();
		constraints.add(8.0);

		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(1);
		expectedNodes.add(3);
		expectedNodes.add(4);
		expectedNodes.add(6);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		ConstrainedPathFinder pathFinder = new LbpsaFeasibleFinder(pathFinderFactory,
				constraintsComparer);

		// Exercise SUT.
		Path actualPath = pathFinder.find(graph, from, to, constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testBnbFindInfLoopGraphConstraintsA() {
		// Assumptions.
		Graph graph = graphFactory.createInfLoop();
		Node from = graph.getNode(0);
		Node to = graph.getNode(6);

		List<Double> constraints = new ArrayList<>();
		constraints.add(18.0);

		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(2);
		expectedNodes.add(3);
		expectedNodes.add(4);
		expectedNodes.add(6);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		ConstrainedPathFinder pathFinder = new LbpsaFeasibleFinder(pathFinderFactory,
				constraintsComparer);

		// Exercise SUT.
		Path actualPath = pathFinder.find(graph, from, to, constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testBnbFindInfLoopGraphConstraintsB() {
		// Assumptions.
		Graph graph = graphFactory.createInfLoop();
		Node from = graph.getNode(0);
		Node to = graph.getNode(6);

		List<Double> constraints = new ArrayList<>();
		constraints.add(8.0);

		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(1);
		expectedNodes.add(3);
		expectedNodes.add(4);
		expectedNodes.add(6);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		ConstrainedPathFinder pathFinder = new LbpsaFeasibleFinder(pathFinderFactory,
				constraintsComparer);

		// Exercise SUT.
		Path actualPath = pathFinder.find(graph, from, to, constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}
}
