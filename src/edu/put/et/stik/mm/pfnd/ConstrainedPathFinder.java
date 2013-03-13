package edu.put.et.stik.mm.pfnd;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;

public interface ConstrainedPathFinder {
	
	Path find(Graph graph, Node from, Node to, List<Double> constraints);
	
}
