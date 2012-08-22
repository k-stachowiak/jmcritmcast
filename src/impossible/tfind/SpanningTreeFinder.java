package impossible.tfind;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Tree;

public interface SpanningTreeFinder {

	Tree find(Node root, Graph graph);

}
