package impossible.helpers;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.SubGraph;
import impossible.model.topology.SubGraphToGraphAdapter;
import impossible.model.topology.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstraintsComparerImpl implements ConstraintsComparer {

	@Override
	public boolean fulfilsAll(SubGraph subGraph, List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) > constraints.get(m - 1))
				return false;
		}
		return true;
	}

	@Override
	public boolean breaksAll(SubGraph subGraph, List<Double> constraints) {
		List<Double> metrics = subGraph.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) <= constraints.get(m - 1))
				return false;
		}
		return true;
	}

	@Override
	public boolean fulfilsIndex(SubGraph subGraph, int m, double constraint) {
		return subGraph.getMetrics().get(m) <= constraint;
	}

	@Override
	public boolean fulfilsAll(Tree tree, Node root, List<Double> constraints) {

		List<Double> accumulator = new ArrayList<>();
		for (int i = 0; i < constraints.size(); ++i) {
			accumulator.add(0.0);
		}

		Set<Node> visited = new HashSet<>();
		Graph asGraph = new SubGraphToGraphAdapter(tree);
		return fulfillsAllRec(asGraph, root, visited, accumulator, constraints);
	}

	private boolean fulfillsAllRec(Graph tree, Node current, Set<Node> visited,
			List<Double> accumulator, List<Double> constraints) {

		visited.add(current);
		
		// Check the constraints.
		for(int m = 0; m < constraints.size(); ++m) {
			if(accumulator.get(m) > constraints.get(m)) {
				return false;
			}
		}

		for (Node neighbor : tree.getNeighbors(current)) {

			// Don't visit nodes twice.
			if (visited.contains(neighbor)) {
				continue;
			}

			Edge edge = tree.getEdge(current.getId(), neighbor.getId());

			// Accumulate edge costs.
			for (int m = 1; m < edge.getMetrics().size(); ++m) {
				accumulator.set(m - 1, accumulator.get(m - 1)
						+ edge.getMetrics().get(m));
			}

			// Recur.
			boolean success = fulfillsAllRec(tree, neighbor, visited,
					accumulator, constraints);			

			// Withdraw edge costs.
			for (int m = 1; m < edge.getMetrics().size(); ++m) {
				accumulator.set(m - 1, accumulator.get(m - 1)
						- edge.getMetrics().get(m));
			}
			
			// Break on failure.
			if(!success) {
				return false;
			}
		}
		
		return true;
	}
}
