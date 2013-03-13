package edu.put.et.stik.mm.tfind;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Tree;


public interface SteinerTreeFinder {

	Tree find(Graph graph, List<Node> group);

}
