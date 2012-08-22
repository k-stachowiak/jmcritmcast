package impossible.helpers.cstrch;

import impossible.model.Graph;
import impossible.model.Node;

import java.util.List;


public interface GroupConstraintsChooser {

	List<Double> choose(Graph graph, List<Node> group);

}
