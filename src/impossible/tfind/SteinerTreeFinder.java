package impossible.tfind;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;

import java.util.List;


public interface SteinerTreeFinder {

	Tree find(Graph graph, List<Node> group);

}
