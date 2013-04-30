package model.topology;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.topology.AdjacencyList.AdjacencyDefinition;


public class AdjacencyListFactory extends GraphFactory {

	@Override
	public Graph createFromLists(List<Node> nodes, List<Edge> edges) {

		// Allocate the object.
		// --------------------
		Map<Node, List<AdjacencyDefinition>> map = new HashMap<>();

		// Generate helper map.
		// --------------------
		Map<Integer, Node> intNodeMap = new HashMap<>();
		for (Node node : nodes)
			intNodeMap.put(node.getId(), node);

		// Process edges.
		// --------------
		for (Edge edge : edges) {
			Node from = intNodeMap.get(edge.getFrom());
			Node to = intNodeMap.get(edge.getTo());

			// Add direct.
			if (!map.containsKey(from))
				map.put(from,
						new ArrayList<AdjacencyList.AdjacencyDefinition>());

			// Add reverse.
			if (!map.containsKey(to))
				map.put(to,
						new ArrayList<AdjacencyList.AdjacencyDefinition>());

			map.get(from).add(
					new AdjacencyList.AdjacencyDefinition(edge, to.getId()));
			
			map.get(to).add(
					new AdjacencyList.AdjacencyDefinition(edge, from.getId()));
		}

		return new AdjacencyList(map);
	}
}
