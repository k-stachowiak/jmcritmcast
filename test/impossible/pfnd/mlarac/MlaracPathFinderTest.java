package impossible.pfnd.mlarac;

import static org.junit.Assert.assertEquals;
import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.model.AdjacencyListFactory;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MlaracPathFinderTest {

	@Test
	public final void test() {

		// Factories.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();

		// Strategies.
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();
		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();
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
		ConstrainedPathFinder pathFinder = pathFinderFactory.createMlarac(pathSubstitutor, lambdaEstimator, constraintsComparer);

		// Exercise SUT.
		Path actualPath = pathFinder.find(graph, from, to, constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

}
