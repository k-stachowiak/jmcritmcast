package impossible.tfind;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;

public interface SpanningTreeFinder {

	Tree find(Node root, Graph graph);

}
