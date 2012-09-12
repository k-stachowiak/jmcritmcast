package impossible.pfnd;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;

import java.util.List;

public interface ConstrainedPathFinder {
	
	Path find(Graph graph, Node from, Node to, List<Double> constraints);
	
}
