package impossible.pfnd.hmcp;

import static org.junit.Assert.*;

import impossible.model.AdjacencyListFactory;
import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class HmcpPathFinderTest {

	@Test
	public final void testFind() {

		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();

		// Input.
		List<Double> constraints = new ArrayList<>();
		constraints.add(10.0);
		constraints.add(10.0);

		// Model.
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0, 1));
		nodes.add(new Node(1, 1, 4));
		nodes.add(new Node(2, 0, 2));
		nodes.add(new Node(3, 2, 4));
		nodes.add(new Node(4, 0, 3));
		nodes.add(new Node(5, 3, 4));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 1.0, 2.0, 3.0 })));
		edges.add(new Edge(0, 2, Arrays.asList(new Double[] { 4.0, 5.0, 6.0 })));
		edges.add(new Edge(1, 2, Arrays.asList(new Double[] { 5.0, 4.0, 3.0 })));
		edges.add(new Edge(1, 3, Arrays.asList(new Double[] { 2.0, 1.0, 2.0 })));
		edges.add(new Edge(1, 4, Arrays.asList(new Double[] { 3.0, 4.0, 5.0 })));
		edges.add(new Edge(2, 5, Arrays.asList(new Double[] { 6.0, 5.0, 4.0 })));
		edges.add(new Edge(3, 5, Arrays.asList(new Double[] { 2.0, 3.0, 4.0 })));
		edges.add(new Edge(4, 5, Arrays.asList(new Double[] { 3.0, 2.0, 1.0 })));

		Graph graph = graphFactory.createFromLists(nodes, edges);

		// Expected.
		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(1);
		expectedNodes.add(4);
		expectedNodes.add(5);

		Path expectedPath = new Path(graph, expectedNodes);

		// Instantiate SUT.
		PathFinder sut = pathFinderFactory.CreateHmcp(constraints);

		// Exercise SUT.
		Path actualPath = sut.find(graph, graph.getNode(0), graph.getNode(5));

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

}
