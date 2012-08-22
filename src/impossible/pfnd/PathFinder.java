package impossible.pfnd;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;

public interface PathFinder {

	Path find(Graph graph, Node from, Node to);

}
