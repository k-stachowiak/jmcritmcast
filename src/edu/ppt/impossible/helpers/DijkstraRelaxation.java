package edu.ppt.impossible.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Path;

public abstract class DijkstraRelaxation {

	protected Map<Node, Node> predecessors;

	public abstract void reset(Graph graph, Node from);

	public abstract void relax(Graph graph, Node from, Node to);

	public abstract boolean isCheaper(Node from, Node to);

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

		return new Path(graph, nodes);
	}
}
