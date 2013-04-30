package edu.put.et.stik.mm.pfnd;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.topology.AdjacencyListFactory;
import model.topology.Edge;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Path;

import org.junit.Test;

import pfnd.PathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;



public class DijkstraPathFinderTest {

	@Test
	public void testFind() {

		// Prepare utilities.
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		PathFinder pathFinder = pathFinderFactory.createDijkstraIndex(0);
		GraphFactory graphFactory = new AdjacencyListFactory();

		// Prepare model.
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0, 0));
		nodes.add(new Node(1, 0, 0));
		nodes.add(new Node(2, 0, 0));
		nodes.add(new Node(3, 0, 0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 1.0, 1.0 })));
		edges.add(new Edge(1, 2, Arrays.asList(new Double[] { 50.0, 1.0 })));
		edges.add(new Edge(2, 3, Arrays.asList(new Double[] { 5.0, 1.0 })));
		edges.add(new Edge(3, 0, Arrays.asList(new Double[] { 5.0, 1.0 })));
		edges.add(new Edge(3, 1, Arrays.asList(new Double[] { 100.0, 1.0 })));

		Graph graph = graphFactory.createFromLists(nodes, edges);
		Node from = graph.getNode(0);
		Node to = graph.getNode(2);

		List<Integer> expectedNodes = new ArrayList<>();
		expectedNodes.add(0);
		expectedNodes.add(3);
		expectedNodes.add(2);

		Path expected = new Path(graph, expectedNodes);

		// Exercise SUT.
		Path actual = pathFinder.find(graph, from, to);

		// Assertions.
		assertEquals(expected, actual);
	}

}
