package edu.put.et.stik.mm.helpers.nodegrp;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

import java.util.List;
import java.util.Random;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;

import org.junit.Test;

public class RandomNodeGroupperTest {

	@Test
	public void testForGroupsAOOB() {

		// Constants
		final int GROUP_SIZE = 3;

		// Helpers.
		Random random = new Random(1);
		NodeGroupper groupper = new RandomNodeGroupper(random);
		GraphFactory graphFactory = new AdjacencyListFactory();

		// Input.
		Graph graphWithMoreThan3Nodes = graphFactory.createMaciejPiechowiakExample();

		// Case.
		List<Node> actualList = groupper.group(graphWithMoreThan3Nodes, GROUP_SIZE);
		int actual = actualList.size();
		int expected = GROUP_SIZE;

		// Assertions.
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testGroup() {

		// Constants.
		final int NON_EXISTENT_NODE_ID_BUT_VALID_INDEX = 0;
		final int GROUP_SIZE = 1;
		
		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();
		
		// Model.
		Graph graph = graphFactory.createNontruncatedNodeIds();

		// Mock random kernel.
		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(
				NON_EXISTENT_NODE_ID_BUT_VALID_INDEX);

		// Instantiate SUT.
		final NodeGroupper sut = new RandomNodeGroupper(random);

		// Exercise SUT.
		sut.group(graph, GROUP_SIZE);		
	}

}
