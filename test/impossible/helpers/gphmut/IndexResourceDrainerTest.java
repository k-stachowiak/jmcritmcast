package impossible.helpers.gphmut;

import static org.junit.Assert.assertEquals;
import impossible.helpers.CostResourceTranslation;
import impossible.helpers.OspfCostResourceTranslation;
import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Edge;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.SubGraph;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IndexResourceDrainerTest {

	@Test
	public void testDrain() {
		
		// Constants.
		// ==========
		final int INDEX = 0;
		final double BASE_BANDWIDTH = 1000.0;
		final double DRAINAGE_STEP = 333.0;
		final double MIN_RESOURCE = 1.0;
		
		// Helpers.
		// ========
		final GraphFactory graphFactory = new AdjacencyListFactory();				
		final CostResourceTranslation costResourceTranslation = new OspfCostResourceTranslation(
				BASE_BANDWIDTH);
		
		// Model.
		// ======
		
		// The base graph.
		final List<Double> METRICS = new ArrayList<>();
		METRICS.add(costResourceTranslation.resourceToCost(BASE_BANDWIDTH));		
		final Graph graph = graphFactory.createOneEdge(METRICS);
		
		// The subgraph to be drained - basically this is a copy of the base graph.
		final List<Integer> sgNodes = new ArrayList<>();
		for(Node n : graph.getNodes()) {
			sgNodes.add(n.getId());
		}
		
		final List<EdgeDefinition> sgEdges = new ArrayList<>();
		for(Edge e : graph.getEdges()) {
			sgEdges.add(new EdgeDefinition(e.getFrom(), e.getTo()));
		}
		
		final SubGraph subGraph = new SubGraph(graph, sgNodes, sgEdges);		

		// Instantiate SUT.
		// ================
		ResourceDrainer resourceDrainer = new IndexResourceDrainer(
				costResourceTranslation, INDEX, graphFactory);
		
		// Exercise SUT.
		// =============
		Graph copy = graph.copy();
		int actualNumDrainsUntilDepletion = 0;
		while(copy.getNumEdges() == 1) {
			copy = resourceDrainer.drain(copy, subGraph, DRAINAGE_STEP, MIN_RESOURCE);
			++actualNumDrainsUntilDepletion;
		}
		
		// Assertions.
		// ===========
		int expectedNumDrainsUntilDepletion = (int)(BASE_BANDWIDTH / DRAINAGE_STEP) + 1;
		assertEquals(expectedNumDrainsUntilDepletion, actualNumDrainsUntilDepletion);
	}

}
