package impossible.tfind.rdp.newimpl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;

import org.junit.Test;

public class SimpleResultBuildProcessTest {

	@Test
	public void testSimpleCase() {

		// Helpers.
		// --------

		GraphFactory graphFactory = new AdjacencyListFactory();

		// Initialize model.
		// -----------------

		// Initialize the graph.
		List<EdgeDefinition> cheapEdges = new ArrayList<>();
		cheapEdges.add(new EdgeDefinition(0, 2));
		cheapEdges.add(new EdgeDefinition(0, 4));
		cheapEdges.add(new EdgeDefinition(0, 6));
		cheapEdges.add(new EdgeDefinition(2, 3));
		cheapEdges.add(new EdgeDefinition(4, 5));
		cheapEdges.add(new EdgeDefinition(6, 1));

		Graph graph = graphFactory.createDoubleQuad(cheapEdges);

		// Prepare the expected tree.
		Tree expectedTree = new Tree(graph, cheapEdges);

		// Node 1 context.
		Node node1 = graph.getNode(1);
		Map<Node, Node> predecessorMap1 = new HashMap<>();
		predecessorMap1.put(graph.getNode(0), graph.getNode(6));
		predecessorMap1.put(graph.getNode(1), graph.getNode(1));
		predecessorMap1.put(graph.getNode(2), graph.getNode(2));
		predecessorMap1.put(graph.getNode(3), graph.getNode(3));
		predecessorMap1.put(graph.getNode(4), graph.getNode(4));
		predecessorMap1.put(graph.getNode(5), graph.getNode(5));
		predecessorMap1.put(graph.getNode(6), graph.getNode(1));

		// Node 3 context.
		Node node3 = graph.getNode(3);
		Map<Node, Node> predecessorMap3 = new HashMap<>();
		predecessorMap3.put(graph.getNode(0), graph.getNode(2));
		predecessorMap3.put(graph.getNode(1), graph.getNode(1));
		predecessorMap3.put(graph.getNode(2), graph.getNode(3));
		predecessorMap3.put(graph.getNode(3), graph.getNode(3));
		predecessorMap3.put(graph.getNode(4), graph.getNode(4));
		predecessorMap3.put(graph.getNode(5), graph.getNode(5));
		predecessorMap3.put(graph.getNode(6), graph.getNode(1));

		// Node 5 context.
		Node node5 = graph.getNode(5);
		Map<Node, Node> predecessorMap5 = new HashMap<>();
		predecessorMap5.put(graph.getNode(0), graph.getNode(4));
		predecessorMap5.put(graph.getNode(1), graph.getNode(1));
		predecessorMap5.put(graph.getNode(2), graph.getNode(2));
		predecessorMap5.put(graph.getNode(3), graph.getNode(3));
		predecessorMap5.put(graph.getNode(4), graph.getNode(5));
		predecessorMap5.put(graph.getNode(5), graph.getNode(5));
		predecessorMap5.put(graph.getNode(6), graph.getNode(1));

		// Cumulative context.
		Map<Node, Map<Node, Node>> predecessorMaps = new HashMap<>();
		predecessorMaps.put(node1, predecessorMap1);
		predecessorMaps.put(node3, predecessorMap3);
		predecessorMaps.put(node5, predecessorMap5);

		// Instantiate SUT.
		// ----------------

		SimpleResultBuildProcess sut = new SimpleResultBuildProcess(graph,
				predecessorMaps, graph.getNode(0));
		
		// Exercise SUT.
		// -------------
		
		Tree actualTree = sut.tryNext();
		Tree actualSecondResult = sut.tryNext();
		
		// Perform assertions.
		// -------------------
		
		assertEquals(expectedTree, actualTree);		
		assertNull(actualSecondResult);
	}

}
