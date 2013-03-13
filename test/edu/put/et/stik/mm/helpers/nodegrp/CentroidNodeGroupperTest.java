package edu.put.et.stik.mm.helpers.nodegrp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

import edu.put.et.stik.mm.helpers.nodegrp.CentroidNodeGroupper;
import edu.put.et.stik.mm.helpers.nodegrp.NodeGroupper;
import edu.put.et.stik.mm.model.topology.AdjacencyListFactory;
import edu.put.et.stik.mm.model.topology.Edge;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.GraphFactory;
import edu.put.et.stik.mm.model.topology.Node;

public class CentroidNodeGroupperTest {

	@Test
	public void testGroup() {
		
		// Constants.
		final double ANY_DOUBLE = 10.0;
		final double CENTROID_X = ANY_DOUBLE;
		final double CENTROID_Y = ANY_DOUBLE;
		final double NEAR_DISTANCE = 1.0;
		final double MEDIUM_DISTANCE = 100.0;
		final double FAR_DISTANCE = 1000.0;
		
		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();
		
		// Model.
		Node nearNode = new Node(0, CENTROID_X + NEAR_DISTANCE, CENTROID_Y + NEAR_DISTANCE);
		Node mediumNode = new Node(1, CENTROID_X + MEDIUM_DISTANCE, CENTROID_Y + MEDIUM_DISTANCE);
		Node farNode = new Node(2, CENTROID_X + FAR_DISTANCE, CENTROID_Y + FAR_DISTANCE);
		
		List<Node> nodes = new ArrayList<>();
		nodes.add(nearNode);
		nodes.add(mediumNode);
		nodes.add(farNode);
		
		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, new ArrayList<Double>()));
		edges.add(new Edge(1, 2, new ArrayList<Double>()));
		
		Graph graph = graphFactory.createFromLists(nodes, edges);
		
		// Instantiate SUT.
		NodeGroupper nodeGroupper = new CentroidNodeGroupper(CENTROID_X, CENTROID_Y);
		
		// Exercise SUT. 
		List<Node> actualNodes1 = nodeGroupper.group(graph, 1);
		assertEquals(actualNodes1.size(), 1);
		assertTrue(actualNodes1.contains(nearNode));
		
		List<Node> actualNodes2 = nodeGroupper.group(graph, 2);
		assertEquals(actualNodes2.size(), 2);
		assertTrue(actualNodes2.contains(nearNode));
		assertTrue(actualNodes2.contains(mediumNode));
		
		List<Node> actualNodes3 = nodeGroupper.group(graph, 3);
		assertEquals(actualNodes3.size(), 3);
		assertTrue(actualNodes3.contains(nearNode));
		assertTrue(actualNodes3.contains(mediumNode));
		assertTrue(actualNodes3.contains(farNode));
	}

}
