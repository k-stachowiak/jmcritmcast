package helpers;


import java.util.List;

import model.topology.Node;
import model.topology.SubGraph;
import model.topology.Tree;


public interface ConstraintsComparer {

	public boolean fulfilsAll(SubGraph subGraph,
			List<Double> constraints);

	public boolean breaksAll(SubGraph subGraph,
			List<Double> constraints);

	public boolean fulfilsIndex(SubGraph subGraph, int m,
			double constraint);

	public boolean fulfilsAll(Tree tree, Node root, List<Double> constraints);
}