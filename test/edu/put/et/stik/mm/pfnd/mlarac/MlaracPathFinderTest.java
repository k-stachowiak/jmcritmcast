package edu.put.et.stik.mm.pfnd.mlarac;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.helpers.ConstraintsComparerImpl;
import edu.put.et.stik.mm.model.topology.AdjacencyListFactory;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.GraphFactory;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;
import edu.put.et.stik.mm.pfnd.PathFinderFactoryImpl;
import edu.put.et.stik.mm.pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import edu.put.et.stik.mm.pfnd.mlarac.IntersectLambdaEstimator;
import edu.put.et.stik.mm.pfnd.mlarac.LambdaEstimator;
import edu.put.et.stik.mm.pfnd.mlarac.PathSubstiutor;

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
