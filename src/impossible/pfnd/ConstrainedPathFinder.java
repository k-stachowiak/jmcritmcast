package impossible.pfnd;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import java.util.List;

public interface ConstrainedPathFinder {
	
	Path find(Graph graph, Node from, Node to, List<Double> constraints);
	
}
