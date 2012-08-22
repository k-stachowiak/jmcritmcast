package impossible.tfind;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Tree;

import java.util.List;


public interface SteinerTreeFinder {

	Tree find(Graph graph, List<Node> group);

}
