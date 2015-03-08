package model.topology;

import static org.junit.Assert.*;
import helpers.TopologyAnalyser;
import helpers.TopologyAnalyserImpl;

import model.topology.AdjacencyListFactory;
import model.topology.AdjacencyMatrixFactory;
import model.topology.Graph;
import model.topology.GraphFactory;

import org.junit.Test;



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
