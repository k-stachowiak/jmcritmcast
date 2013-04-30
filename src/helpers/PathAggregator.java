package helpers;


import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;
import model.topology.Tree;


public interface PathAggregator {

	public abstract Tree aggregate(Graph graph, Node root, List<Path> paths);

}