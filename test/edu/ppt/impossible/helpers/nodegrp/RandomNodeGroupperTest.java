package edu.ppt.impossible.helpers.nodegrp;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import edu.ppt.impossible.helpers.nodegrp.NodeGroupper;
import edu.ppt.impossible.helpers.nodegrp.RandomNodeGroupper;
import edu.ppt.impossible.model.AdjacencyListFactory;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.GraphFactory;
import edu.ppt.impossible.model.Node;

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
		Graph graph = graphFactory.createTest();

		// Case.
		List<Node> actualList = groupper.group(graph, GROUP_SIZE);
		int actual = actualList.size();
		int expected = GROUP_SIZE;

		// Assertions.
		assertEquals(expected, actual);
	}

}
