package impossible.helpers;

import impossible.model.topology.Node;
import impossible.model.topology.SubGraph;
import impossible.model.topology.Tree;

import java.util.List;

public interface ConstraintsComparer {

	public boolean fulfilsAll(SubGraph subGraph,
			List<Double> constraints);

	public boolean breaksAll(SubGraph subGraph,
			List<Double> constraints);

	public boolean fulfilsIndex(SubGraph subGraph, int m,
			double constraint);

	public boolean fulfilsAll(Tree tree, Node root, List<Double> constraints);
}