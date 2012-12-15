package impossible.tfind.xamcra;

import static org.junit.Assert.*;
import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Path;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class XamcraCommonTest {
	
	private static final int ANY_INT = -1;

	@Test
	public void testIsDominatedBy() {

		final double smallDbl = 1.0;
		final double mediumDbl = 10.0;
		final double bigDbl = 100.0;

		// Model.
		Double[] tested = new Double[] { mediumDbl, mediumDbl };
		Double[] dominating = new Double[] { smallDbl, smallDbl };
		Double[] nonDominating = new Double[] { smallDbl, bigDbl };

		// Instantiate SUT.
		XamcraCommon sut = new XamcraCommon();

		// Assertions.
		assertTrue(sut.isDominatedBy(Arrays.asList(tested),
				Arrays.asList(dominating)));
		
		assertFalse(sut.isDominatedBy(Arrays.asList(tested),
				Arrays.asList(nonDominating)));
	}

	@Test
	public void testBuildPath() {
		
		final double anyDouble = -1.0;
		
		// Prepare the expected path.
		List<Double> anyEdgeMetrics = Arrays.asList(new Double[] { anyDouble });
		GraphFactory graphFactory = new AdjacencyListFactory();
		Path expectedPath = graphFactory.createPath(2, anyEdgeMetrics);
		Graph parent = expectedPath.getParent();
			
		// Prepare the input path definition.
		PathNode pathNode = null;
		for(Integer id : expectedPath.getNodes()) {
			pathNode = new PathNode(parent.getNode(id), ANY_INT, pathNode);
		}
		
		// Instantiate sut.
		XamcraCommon sut = new XamcraCommon();
		
		// Exercise sut.
		Path actualPath = sut.buildPath(pathNode, parent);
		
		// Assertions.
		assertEquals(expectedPath, actualPath);
	}


}
