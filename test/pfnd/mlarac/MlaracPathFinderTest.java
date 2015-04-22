package pfnd.mlarac;

import static org.junit.Assert.assertEquals;

import helpers.ConstraintsComparer;

import java.util.ArrayList;
import java.util.List;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Path;

import org.junit.Test;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;
import pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import pfnd.mlarac.IntersectLambdaEstimator;
import pfnd.mlarac.LambdaEstimator;
import pfnd.mlarac.PathSubstiutor;


public class MlaracPathFinderTest {

	@Test
	public final void test() {

		// Factories.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();

		// Strategies.
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();
		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();
		ConstraintsComparer constraintsComparer = new ConstraintsComparer();

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
		ConstrainedPathFinder pathFinder = pathFinderFactory.createMlarac(pathSubstitutor, lambdaEstimator, constraintsComparer);

		// Exercise SUT.
		Path actualPath = pathFinder.find(graph, from, to, constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

}
