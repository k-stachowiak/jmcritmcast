package impossible.pfnd;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class CommonRelaxation {

	protected String predecessorsString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("p\t");
		for(Map.Entry<Node, Node> entry : predecessors.entrySet()) {
			stringBuilder.append(entry.getValue().getId() + "\t");
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	protected Map<Node, Node> predecessors;

	public abstract void reset(Graph graph, Node from);

	public abstract boolean relax(Graph graph, Node from, Node to);

	public abstract boolean isCheaper(Node a, Node b);

	public Path buildPath(Graph graph, Node from, Node to) {

		List<Integer> nodes = new ArrayList<>();
		Node current = to;

		while (!current.equals(from)) {

			if (predecessors.get(current).equals(current)
					&& !current.equals(from))
				return null;

			nodes.add(current.getId());
			current = predecessors.get(current);
		}

		nodes.add(from.getId());

		Collections.reverse(nodes);

		return new Path(graph, nodes);
	}
}
