package edu.put.et.stik.mm.model.topology;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.put.et.stik.mm.helpers.TopologyAnalyser;
import edu.put.et.stik.mm.helpers.TopologyAnalyserImpl;
import edu.put.et.stik.mm.model.topology.AdjacencyListFactory;
import edu.put.et.stik.mm.model.topology.AdjacencyMatrixFactory;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.GraphFactory;


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
