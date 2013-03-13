package edu.put.et.stik.mm.helpers.cstrch;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;


public interface GroupConstraintsChooser {

	List<Double> choose(Graph graph, List<Node> group);

}
