package pfnd;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;

public interface PathFinder {

	Path find(Graph graph, Node from, Node to);

}
