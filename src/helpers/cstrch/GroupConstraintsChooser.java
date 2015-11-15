package helpers.cstrch;


import java.util.List;

import model.topology.Graph;
import model.topology.Node;

public interface GroupConstraintsChooser {

	List<Double> choose(Graph graph, List<Node> group);

}
