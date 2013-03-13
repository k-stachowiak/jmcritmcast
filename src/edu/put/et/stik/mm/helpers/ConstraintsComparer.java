package edu.put.et.stik.mm.helpers;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.SubGraph;
import edu.put.et.stik.mm.model.topology.Tree;

public interface ConstraintsComparer {

	public boolean fulfilsAll(SubGraph subGraph,
			List<Double> constraints);

	public boolean breaksAll(SubGraph subGraph,
			List<Double> constraints);

	public boolean fulfilsIndex(SubGraph subGraph, int m,
			double constraint);

	public boolean fulfilsAll(Tree tree, Node root, List<Double> constraints);
}