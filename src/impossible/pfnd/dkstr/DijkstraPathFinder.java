package impossible.pfnd.dkstr;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.pfnd.CommonRelaxation;
import impossible.pfnd.PathFinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DijkstraPathFinder implements PathFinder {

	private final CommonRelaxation relaxation;

	public DijkstraPathFinder(CommonRelaxation relaxation) {
		this.relaxation = relaxation;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		Set<Node> open = new HashSet<>();
		Set<Node> closed = new HashSet<>();

		open.add(from);

		relaxation.reset(graph, from);

		while (!open.isEmpty()) {

			Node current = cheapest(open);
			open.remove(current);
			closed.add(current);

			List<Node> neighbors = graph.getNeighbors(current);

			for (Node neighbor : neighbors) {
				if (closed.contains(neighbor))
					continue;

				boolean relaxed = relaxation.relax(graph, current,
						neighbor);
				
				if (relaxed) {
					open.add(neighbor);
				}
			}
		}

		return relaxation.buildPath(graph, from, to);
	}

	private Node cheapest(Set<Node> open) {
		Node candidate = null;
		for (Node node : open) {
			if (candidate == null
					|| relaxation.isCheaper(node, candidate)) {

				candidate = node;
			}
		}
		return candidate;
	}

}
