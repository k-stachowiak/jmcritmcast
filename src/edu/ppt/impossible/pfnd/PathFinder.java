package edu.ppt.impossible.pfnd;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Path;

public interface PathFinder {

	Path find(Graph graph, Node from, Node to);

}
