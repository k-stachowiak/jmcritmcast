package impossible.tfind.xamcra;

import static org.junit.Assert.*;
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

public class SamcraTest {

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
		Samcra sut = new Samcra();

		// Exercise SUT.
		Path actualPath = sut.find(graph, graph.getNode(4), graph.getNode(2),
				constraints);

		// Assertions.
		assertEquals(expectedPath, actualPath);
	}

}
