package impossible.helpers;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.model.topology.Tree;

import java.util.List;

public interface PathAggregator {

	public abstract Tree aggregate(Graph graph, Node root, List<Path> paths);

}