package impossible.tfind.rdp.newimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;

public class SimpleResultBuildProcess implements ResultBuildProcess {

	// External dependencies.
	private final Graph graph;
	private final Map<Node, Map<Node, Node>> predecessorMaps; // [source][node]
	private final Node target;

	// State.
	boolean wasRead;

	public SimpleResultBuildProcess(Graph graph,
			Map<Node, Map<Node, Node>> predecessorMaps, Node target) {
		this.graph = graph;
		this.predecessorMaps = predecessorMaps;
		this.target = target;
		wasRead = false;
	}

	@Override
	public Tree tryNext() {

		// Only allow a single attempt at building the result.
		if (wasRead) {
			return null;
		}

		// Prevent further tries.
		wasRead = true;

		// Build the result.
		List<EdgeDefinition> edgeDefinitions = new ArrayList<>();

		// Append the edges of the paths towards each of the multicast group
		// members.
		for (Node source : predecessorMaps.keySet()) {

			if (source.equals(target)) {
				// No need to build path.
				continue;
			}

			Map<Node, Node> preds = predecessorMaps.get(source);
			Node current = target;
			Node next = null;

			do {
				// Look ahead and detect failure.
				next = preds.get(current);
				if (current.equals(next)) {
					return null;
				}

				// Append an edge to the result.
				edgeDefinitions.add(new EdgeDefinition(next.getId(), current
						.getId()));

				current = next;

			} while (!next.equals(source));
		}

		return new Tree(graph, edgeDefinitions);
	}
}
