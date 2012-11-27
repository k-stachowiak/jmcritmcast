package impossible.tfind.xamcra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TamcraTest {
	
	private static final int ANY_INT = -1;

	@Test
	public void testSimpleFind() {
		
		// Metrics.
		double constraint1 = 2.0;
		double less1 = constraint1 - 1.0;
		double more1 = constraint1 + 1.0;
		
		double constraint2 = 4.0;
		double less2 = constraint2 - 1.0;		
		double more2 = constraint2 + 1.0;

		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();

		// Input.
		List<Double> constraints = new ArrayList<>();
		constraints.add(constraint1);
		constraints.add(constraint2);

		// Model.		
		List<Double> cheaperPathMetrics = new ArrayList<>();
		cheaperPathMetrics.add(less1);
		cheaperPathMetrics.add(less2);
		
		List<Double> expensivePathMetrics = new ArrayList<>();
		expensivePathMetrics.add(more1);
		expensivePathMetrics.add(more2);
		
		List<List<Double>> metrics = new ArrayList<>();
		metrics.add(cheaperPathMetrics);
		metrics.add(expensivePathMetrics);

		Graph graph = graphFactory.createNSimplePathNm(metrics);

		// Expected.
		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(1);
		expectedNodes.add(3);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		Tamcra sut = new Tamcra(10);

		// Exercise SUT.
		Path actualPath = sut.find(graph, graph.getNode(0), graph.getNode(3),
				constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testIntermediateFind() {
		
		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();

		// Input.
		List<Double> constraints = new ArrayList<>();
		constraints.add(1000.0);
		constraints.add(2000.0);

		// Model.
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0, 0));
		nodes.add(new Node(1, 0, 0));
		nodes.add(new Node(2, 0, 0));
		nodes.add(new Node(3, 0, 0));
		nodes.add(new Node(4, 0, 0));
		nodes.add(new Node(5, 0, 0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(4, 1, Arrays.asList(new Double[] { 928.9648196112299, 197.4927115098638 })));
		edges.add(new Edge(3, 2, Arrays.asList(new Double[] { 111.16333789774005, 762.370936669542 })));
		edges.add(new Edge(3, 1, Arrays.asList(new Double[] { 43.13650526777225, 379.24425248524807 })));
		edges.add(new Edge(4, 3, Arrays.asList(new Double[] { 880.7180711195269, 630.7710393442063 })));
		edges.add(new Edge(1, 2, Arrays.asList(new Double[] { 786.8226365360263, 713.0394155983274 })));
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 774.8295053494649, 307.17630531723455 })));
		edges.add(new Edge(0, 2, Arrays.asList(new Double[] { 754.8915990158057, 158.47298319382946 })));

		Graph graph = graphFactory.createFromLists(nodes, edges);

		// Expected.
		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(4);
		expectedNodes.add(3);
		expectedNodes.add(2);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		Tamcra sut = new Tamcra(10);

		// Exercise SUT.
		Path actualPath = sut.find(graph, graph.getNode(4), graph.getNode(2),
				constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

	@Test
	public void testIsDominatedBy() {

		final double smallDbl = 1.0;
		final double mediumDbl = 10.0;
		final double bigDbl = 100.0;

		// Model.
		Double[] tested = new Double[] { mediumDbl, mediumDbl };
		Double[] dominating = new Double[] { smallDbl, smallDbl };
		Double[] nonDominating = new Double[] { smallDbl, bigDbl };

		// Instantiate SUT.
		Tamcra sut = new Tamcra(ANY_INT);

		// Assertions.
		assertTrue(sut.isDominatedBy(Arrays.asList(tested),
				Arrays.asList(dominating)));
		
		assertFalse(sut.isDominatedBy(Arrays.asList(tested),
				Arrays.asList(nonDominating)));
	}

	@Test
	public void testBuildPath() {
		
		final double anyDouble = -1.0;
		
		// Prepare the expected path.
		List<Double> anyEdgeMetrics = Arrays.asList(new Double[] { anyDouble });
		GraphFactory graphFactory = new AdjacencyListFactory();
		Path expectedPath = graphFactory.createPath(2, anyEdgeMetrics);
		Graph parent = expectedPath.getParent();
			
		// Prepare the input path definition.
		PathNode pathNode = null;
		for(Integer id : expectedPath.getNodes()) {
			pathNode = new PathNode(parent.getNode(id), ANY_INT, pathNode);
		}
		
		// Instantiate sut.
		Tamcra sut = new Tamcra(ANY_INT);
		
		// Exercise sut.
		Path actualPath = sut.buildPath(pathNode, parent);
		
		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

}
