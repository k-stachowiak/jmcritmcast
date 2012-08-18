package edu.ppt.impossible.helpers.cstrch;

import java.util.List;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;

public interface GroupConstraintsChooser {

	List<Double> choose(Graph graph, List<Node> group);

}
