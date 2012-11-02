package impossible.tfind.rdp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.ConstrainedSteinerTreeFinder;

import org.junit.Test;

public class RdpTreeFinderTest {

	@Test
	public void testFind() {
		
		GraphFactory graphFactory = new AdjacencyListFactory();
		
		List<EdgeDefinition> cheapEdges = new ArrayList<>();
		cheapEdges.add(new EdgeDefinition(0, 3));
		cheapEdges.add(new EdgeDefinition(3, 4));
		cheapEdges.add(new EdgeDefinition(1, 4));
		cheapEdges.add(new EdgeDefinition(3, 5));
		cheapEdges.add(new EdgeDefinition(5, 2));
		
		Graph graph = graphFactory.createDoubleTriangle(cheapEdges);
		
		List<Node> group = new ArrayList<>();
		group.add(graph.getNode(0));
		group.add(graph.getNode(1));
		group.add(graph.getNode(2));
		
		List<Double> constraints = new ArrayList<>();
		constraints.add(20.0);
		constraints.add(20.0);
		
		ConstrainedSteinerTreeFinder finder = new RdpTreeFinder();
		Tree result = finder.find(graph, group, constraints);
		
		assertNotNull(result);
	}

}
