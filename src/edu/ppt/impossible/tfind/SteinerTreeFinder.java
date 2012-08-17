package edu.ppt.impossible.tfind;

import java.util.List;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Tree;

public interface SteinerTreeFinder {

	Tree find(Graph graph, List<Node> group);

}
