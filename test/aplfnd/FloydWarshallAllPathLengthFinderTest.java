package aplfnd;

import static org.junit.Assert.assertEquals;
import helpers.PathMetric;
import helpers.metrprov.IndexMetricProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import model.topology.AdjacencyListFactory;
import model.topology.Edge;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.NodePair;

import org.junit.Test;

public class FloydWarshallAllPathLengthFinderTest {

	private static final double ANY_DOUBLE = 0.0;

	@Test
	public void test() {

		GraphFactory graphFactory = new AdjacencyListFactory();

		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, ANY_DOUBLE, ANY_DOUBLE));
		nodes.add(new Node(1, ANY_DOUBLE, ANY_DOUBLE));
		nodes.add(new Node(2, ANY_DOUBLE, ANY_DOUBLE));
		nodes.add(new Node(3, ANY_DOUBLE, ANY_DOUBLE));
		nodes.add(new Node(4, ANY_DOUBLE, ANY_DOUBLE));

		ArrayList<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 10.0 })));
		edges.add(new Edge(0, 3, Arrays.asList(new Double[] { 5.0 })));
		edges.add(new Edge(1, 2, Arrays.asList(new Double[] { 5.0 })));
		edges.add(new Edge(1, 3, Arrays.asList(new Double[] { 5.0 })));
		edges.add(new Edge(1, 4, Arrays.asList(new Double[] { 10.0 })));
		edges.add(new Edge(3, 4, Arrays.asList(new Double[] { 20.0 })));

		Graph graph = graphFactory.createFromLists(nodes, edges);

		IndexMetricProvider imp = new IndexMetricProvider(0);

		FloydWarshallAllPathLengthFinder sut = new FloydWarshallAllPathLengthFinder();

		Map<NodePair, PathMetric> lengths = sut.find(graph, imp);

		assertEquals(10.0, lengths
				.get(new NodePair(nodes.get(0), nodes.get(1))).getCost(), 0.1);
		assertEquals(15.0, lengths
				.get(new NodePair(nodes.get(0), nodes.get(2))).getCost(), 0.1);
		assertEquals(5.0, lengths.get(new NodePair(nodes.get(0), nodes.get(3)))
				.getCost(), 0.1);
		assertEquals(20.0, lengths
				.get(new NodePair(nodes.get(0), nodes.get(4))).getCost(), 0.1);
		assertEquals(5.0, lengths.get(new NodePair(nodes.get(1), nodes.get(2)))
				.getCost(), 0.1);
		assertEquals(5.0, lengths.get(new NodePair(nodes.get(1), nodes.get(3)))
				.getCost(), 0.1);
		assertEquals(10.0, lengths
				.get(new NodePair(nodes.get(1), nodes.get(4))).getCost(), 0.1);
		assertEquals(10.0, lengths
				.get(new NodePair(nodes.get(2), nodes.get(3))).getCost(), 0.1);
		assertEquals(15.0, lengths
				.get(new NodePair(nodes.get(2), nodes.get(4))).getCost(), 0.1);
		assertEquals(15.0, lengths
				.get(new NodePair(nodes.get(3), nodes.get(4))).getCost(), 0.1);
	}

}