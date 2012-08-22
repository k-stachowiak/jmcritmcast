package impossible.helpers;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.model.Tree;

import java.util.List;

public interface PathAggregator {

	public abstract Tree aggregate(Graph graph, Node root, List<Path> paths);

}