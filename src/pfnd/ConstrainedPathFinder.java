package pfnd;


import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;


public interface ConstrainedPathFinder {
	
	Path find(Graph graph, Node from, Node to, List<Double> constraints);
	
}
