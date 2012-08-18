package edu.ppt.impossible.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjacencyListFactory extends GraphFactory {

	@Override
	public Graph createFromLists(List<Node> nodes, List<Edge> edges) {

		// Allocate the object.
		// --------------------
		AdjacencyList result = new AdjacencyList();
		result.map = new HashMap<>();

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
			if (!result.map.containsKey(from))
				result.map.put(from,
						new ArrayList<AdjacencyList.AdjacencyDefinition>());

			// Add reverse.
			if (!result.map.containsKey(to))
				result.map.put(to,
						new ArrayList<AdjacencyList.AdjacencyDefinition>());

			result.map.get(from).add(
					new AdjacencyList.AdjacencyDefinition(edge, to.getId()));
			
			result.map.get(to).add(
					new AdjacencyList.AdjacencyDefinition(edge, from.getId()));
		}

		return result;
	}
}
