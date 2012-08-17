package edu.ppt.impossible.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ppt.impossible.model.AdjacencyList.AdjacencyDefinition;

public class AdjacencyListFactory extends GraphFactory {

	@Override
	public Graph createFromLists(List<Node> nodes, List<Edge> edges) {

		AdjacencyList result = new AdjacencyList();

		Map<Integer, Node> intNodeMap = new HashMap<>();
		for (Node node : nodes)
			intNodeMap.put(node.getId(), node);

		for (Edge edge : edges) {
			Node from = intNodeMap.get(edge.getFrom());
			Node to = intNodeMap.get(edge.getTo());

			// Add direct.
			if (!result.map.containsKey(from))
				result.map.put(from,
						new ArrayList<AdjacencyList.AdjacencyDefinition>());

			result.map.get(from).add(new AdjacencyDefinition(edge, to.getId()));

			// Add reverse.
			if (!result.map.containsKey(to))
				result.map.put(to,
						new ArrayList<AdjacencyList.AdjacencyDefinition>());

			result.map.get(to).add(new AdjacencyDefinition(edge, from.getId()));
		}

		return result;
	}
}
