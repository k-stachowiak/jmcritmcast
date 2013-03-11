package impossible.model.topology;

import static org.junit.Assert.*;

import org.junit.Test;

import impossible.helpers.TopologyAnalyser;
import impossible.helpers.TopologyAnalyserImpl;

public class GraphImplConsistencyTest {

	@Test
	public void testGraphCreation() {
		
		GraphFactory listFac = new AdjacencyListFactory();
		Graph listBasedGraph = listFac.createBig2Metr();
		
		GraphFactory matFac = new AdjacencyMatrixFactory();
		Graph matBasedGraph = matFac.createBig2Metr();
		
		TopologyAnalyser analyser = new TopologyAnalyserImpl();
		
		assertTrue(analyser.equal(listBasedGraph, matBasedGraph));
	}
	
}
