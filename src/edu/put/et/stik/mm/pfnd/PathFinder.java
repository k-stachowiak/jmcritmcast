package edu.put.et.stik.mm.pfnd;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;

public interface PathFinder {

	Path find(Graph graph, Node from, Node to);

}
