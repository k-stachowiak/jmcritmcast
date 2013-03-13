package edu.put.et.stik.mm.tfind;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Tree;

public interface SpanningTreeFinder {

	Tree find(Node root, Graph graph);

}
