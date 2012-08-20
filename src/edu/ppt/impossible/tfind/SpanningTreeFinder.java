package edu.ppt.impossible.tfind;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Tree;

public interface SpanningTreeFinder {

	Tree find(Node root, Graph graph);

}
