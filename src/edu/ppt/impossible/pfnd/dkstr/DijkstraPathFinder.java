package edu.ppt.impossible.pfnd.dkstr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Path;
import edu.ppt.impossible.pfnd.PathFinder;

public class DijkstraPathFinder implements PathFinder {

	private final DijkstraRelaxation dijkstraRelaxation;

	public DijkstraPathFinder(DijkstraRelaxation dijkstraRelaxation) {
		this.dijkstraRelaxation = dijkstraRelaxation;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		Set<Node> open = new HashSet<>();
		Set<Node> closed = new HashSet<>();

		open.add(from);

		dijkstraRelaxation.reset(graph, from);

		while (!open.isEmpty()) {

			Node current = cheapest(open);
			open.remove(current);
			closed.add(current);

			List<Node> neighbors = graph.getNeighbors(current);

			for (Node neighbor : neighbors) {
				if (closed.contains(neighbor))
					continue;

				boolean relaxed = dijkstraRelaxation.relax(graph, current,
						neighbor);
				
				if (relaxed) {
					open.add(neighbor);
				}
			}
		}

		return dijkstraRelaxation.buildPath(graph, from, to);
	}

	private Node cheapest(Set<Node> open) {
		Node candidate = null;
		for (Node node : open) {
			if (candidate == null
					|| dijkstraRelaxation.isCheaper(node, candidate)) {

				candidate = node;
			}
		}
		return candidate;
	}

}