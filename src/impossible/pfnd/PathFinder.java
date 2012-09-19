package impossible.pfnd;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

public interface PathFinder {

	Path find(Graph graph, Node from, Node to);

}
