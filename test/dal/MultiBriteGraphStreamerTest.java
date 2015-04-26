package dal;

import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;

import dto.GraphDTO;

public class MultiBriteGraphStreamerTest {
	
	private static String EXAMPLE_SOURCE =
		"Topology: ( 2 Nodes, 1 Edges )\n" +
		"Model (4 - ASBarabasi):  50 1000 100 1  2  1 10.0 1024.0\n" + 
		"\n" +
		"Nodes: ( 2 )\n" +
		"0	123	456	14	14	0	AS_NODE\n" +
		"1	654	321	14	14	0	AS_NODE\n" +
		"\n" +
		"\n" +
		"Edges: ( 1 )" +
		"0	0	1	123.456	654.321	10.0	0	1	E_AS	U\n";

	@Test
	public void testMultiBriteGraphStreamer() {
		
		GraphDTO result = MultiBriteGraphStreamer.readGraph(new Scanner(EXAMPLE_SOURCE));
		
		assertNotNull(result);
		
		assertEquals(2, result.getNodes().size());
		
		assertEquals(0, result.getNodes().get(0).getId());
		assertEquals(123, result.getNodes().get(0).getX(), 0.1);
		assertEquals(456, result.getNodes().get(0).getY(), 0.1);
		
		assertEquals(1, result.getNodes().get(1).getId());
		assertEquals(654, result.getNodes().get(1).getX(), 0.1);
		assertEquals(321, result.getNodes().get(1).getY(), 0.1);
		
		assertEquals(1, result.getEdges().size());
		
		assertEquals(0, result.getEdges().get(0).getNodeFrom());
		assertEquals(1, result.getEdges().get(0).getNodeTo());
		assertEquals(2, result.getEdges().get(0).getMetrics().size());
		assertEquals(123.456, result.getEdges().get(0).getMetrics().get(0), 0.1);
		assertEquals(654.321, result.getEdges().get(0).getMetrics().get(1), 0.1);
	}

}
