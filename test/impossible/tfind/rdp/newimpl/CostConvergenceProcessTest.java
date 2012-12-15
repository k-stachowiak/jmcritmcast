package impossible.tfind.rdp.newimpl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;

import org.junit.Test;

public class CostConvergenceProcessTest {

	@Test
	public void testSimpleCase() {

		/*
		 * Focus on the event times. Create a path that will guarantee the steps
		 * direction and the successive accumulated costs.
		 */

		// Prepare the edge costs.
		// -----------------------
		List<Double> edge1 = Arrays.asList(new Double[] { 1.0 });
		List<Double> edge2 = Arrays.asList(new Double[] { 2.0 });
		List<Double> edge3 = Arrays.asList(new Double[] { 3.0 });

		List<List<Double>> allMetrics = new ArrayList<>();
		allMetrics.add(edge1);
		allMetrics.add(edge2);
		allMetrics.add(edge3);

		// Build the model.
		// ----------------
		GraphFactory graphFactory = new AdjacencyListFactory();
		Graph graph = graphFactory.createPathGraph(allMetrics);

		// ACHTUNG! It is assumed that the factory assigns id 0
		// to the first node in the path.
		Node source = graph.getNode(0);

		// Instantiate SUT.
		Map<Node, Double> costMap = new HashMap<>();
		Map<Node, Node> predecessorMap = new HashMap<>();
		CostConvergenceProcess sut = new CostConvergenceProcess(graph, costMap,
				predecessorMap, source);

		// Prepare the expected record:
		// ----------------------------

		// The partial sums of the subsequences of the edge costs.
		List<Double> expectedSimTimes = Arrays.asList(new Double[] { 0.0, 1.0,
				3.0, 6.0 });

		// Get 4 nodes, as 3 edge costs were defined.
		List<Node> expectedNodesSeq = Arrays.asList(new Node[] {
				graph.getNode(0), graph.getNode(1), graph.getNode(2),
				graph.getNode(3), });

		// Exercise SUT
		// ------------
		List<Double> actualSimTimes = new ArrayList<>();
		List<Node> actualNodesSeq = new ArrayList<>();

		// Another node.
		actualSimTimes.add(sut.nextEventTime());
		actualNodesSeq.add(sut.handleNextEvent());

		// Another node.
		actualSimTimes.add(sut.nextEventTime());
		actualNodesSeq.add(sut.handleNextEvent());

		// Another node.
		actualSimTimes.add(sut.nextEventTime());
		actualNodesSeq.add(sut.handleNextEvent());

		// Another node.
		actualSimTimes.add(sut.nextEventTime());
		actualNodesSeq.add(sut.handleNextEvent());

		// Perform assertions.
		assertEquals(expectedSimTimes, actualSimTimes);
		assertEquals(expectedNodesSeq, actualNodesSeq);
		assertTrue(sut.isDone());
	}

	@Test
	public void testSimpleSearch() {

		// Prepare the model.
		// ------------------
		List<EdgeDefinition> cheapEdges = Arrays.asList(new EdgeDefinition[] {
				new EdgeDefinition(0, 3), new EdgeDefinition(0, 5),
				new EdgeDefinition(3, 1), new EdgeDefinition(3, 4),
				new EdgeDefinition(4, 2), });

		GraphFactory graphFactory = new AdjacencyListFactory();
		Graph graph = graphFactory.createDoubleTriangle(cheapEdges);

		// ACHTUNG! It is assumed that the factory assigns id 0
		// to the specific node in the triangle.
		Node source = graph.getNode(0);

		// Instantiate SUT.
		// ----------------
		Map<Node, Double> actualCostMap = new HashMap<>();
		Map<Node, Node> actualPredecessorMap = new HashMap<>();
		CostConvergenceProcess sut = new CostConvergenceProcess(graph, actualCostMap,
				actualPredecessorMap, source);

		// Exercise SUT.
		// -------------
		while(!sut.isDone()) {
			sut.handleNextEvent();
		}
		
		// Perform assertions.
		// -------------------
		Map<Node, Node> expectedPredecessorMap = new HashMap<>();
		expectedPredecessorMap.put(graph.getNode(0), graph.getNode(0));
		expectedPredecessorMap.put(graph.getNode(1), graph.getNode(3));
		expectedPredecessorMap.put(graph.getNode(2), graph.getNode(4));
		expectedPredecessorMap.put(graph.getNode(3), graph.getNode(0));
		expectedPredecessorMap.put(graph.getNode(4), graph.getNode(3));
		expectedPredecessorMap.put(graph.getNode(5), graph.getNode(0));
		
		Map<Node, Double> expectedCostMap = new HashMap<>();
		expectedCostMap.put(graph.getNode(0), 0.0);
		expectedCostMap.put(graph.getNode(1), 10.0);
		expectedCostMap.put(graph.getNode(2), 15.0);
		expectedCostMap.put(graph.getNode(3), 5.0);
		expectedCostMap.put(graph.getNode(4), 10.0);
		expectedCostMap.put(graph.getNode(5), 5.0);
		
		assertEquals(expectedPredecessorMap, actualPredecessorMap);
		assertEquals(expectedCostMap, actualCostMap);
	}
	
	boolean compareMaps(Map<?, ?> lhs, Map<?, ?> rhs) {
		
		// Fail on different sizes.
		if(lhs.size() != rhs.size()) {
			return false;
		}
		
		// Now looking for all the lhs's keys in rhs's keys
		// covers the keys equality.
		for(Object key : lhs.keySet()) {
			
			// Fail on a key not found.
			if(!rhs.containsKey(key)) {
				return false;
			}
			
			// Keys math, compare the associated values.
			Object lValue = lhs.get(key);
			Object rValue = rhs.get(key);			
			if(!lValue.equals(rValue)) {
				return false;
			}
		}
		
		// Success.
		return true;
	}

}
