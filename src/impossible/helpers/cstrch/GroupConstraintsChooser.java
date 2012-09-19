package impossible.helpers.cstrch;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;

import java.util.List;


public interface GroupConstraintsChooser {

	List<Double> choose(Graph graph, List<Node> group);

}
