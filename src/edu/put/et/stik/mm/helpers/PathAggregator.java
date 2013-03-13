package edu.put.et.stik.mm.helpers;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.model.topology.Tree;

public interface PathAggregator {

	public abstract Tree aggregate(Graph graph, Node root, List<Path> paths);

}