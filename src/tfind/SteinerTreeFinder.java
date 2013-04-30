package tfind;


import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Tree;



public interface SteinerTreeFinder {

	Tree find(Graph graph, List<Node> group);

}
