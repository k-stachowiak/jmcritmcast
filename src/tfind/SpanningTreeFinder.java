package tfind;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Tree;

public interface SpanningTreeFinder {

	Tree find(Node root, Graph graph);

}
