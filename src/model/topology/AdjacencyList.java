package model.topology;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdjacencyList implements Graph {

	static class AdjacencyDefinition {

		private final Edge edge;
		private final int destination;

		public AdjacencyDefinition(Edge edge, int destination) {
			this.edge = edge;
			this.destination = destination;
		}

		public Edge getEdge() {
			return edge;
		}

		public int getDestination() {
			return destination;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + destination;
			result = prime * result + ((edge == null) ? 0 : edge.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AdjacencyDefinition other = (AdjacencyDefinition) obj;
			if (destination != other.destination)
				return false;
			if (edge == null) {
				if (other.edge != null)
					return false;
			} else if (!edge.equals(other.edge))
				return false;
			return true;
		}
	}

	Map<Node, List<AdjacencyDefinition>> map;
	
	AdjacencyList(Map<Node, List<AdjacencyDefinition>> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Node node : map.keySet()) {
			sb.append(node + ":\n");
			for(AdjacencyDefinition ad : map.get(node)) {
				sb.append("\t -> " + ad.getDestination() + "\n");
			}
		}
		return sb.toString();
	}

	@Override
	public Graph copy() {
		Map<Node, List<AdjacencyDefinition>> map = new HashMap<>(this.map);
		return new AdjacencyList(map);
	}

	@Override
	public int getNumNodes() {
		return map.keySet().size();
	}

	@Override
	public int getNumEdges() {
		Set<Edge> uniqueEdges = new HashSet<>();
		for(List<AdjacencyDefinition> definitionsList : map.values()) {
			for(AdjacencyDefinition definition : definitionsList) {
				uniqueEdges.add(definition.getEdge());
			}
		}
		return uniqueEdges.size();
	}

	@Override
	public int getNumMetrics() {
		Iterator<Map.Entry<Node, List<AdjacencyDefinition>>> iterator = map
				.entrySet().iterator();

		if (!iterator.hasNext())
			throw new RuntimeException(
					"Getting number of metrics from an empty adjacency list.");

		while (iterator.hasNext()) {
			List<AdjacencyDefinition> adjacencyDefinitions = iterator.next()
					.getValue();

			if (adjacencyDefinitions.isEmpty())
				continue;

			AdjacencyDefinition adjacencyDefinition = adjacencyDefinitions
					.get(0);

			Edge edge = adjacencyDefinition.getEdge();

			List<Double> metrics = edge.getMetrics();

			if (metrics.isEmpty())
				throw new RuntimeException(
						"Edge with empty metrics list encountered.");

			return metrics.size();
		}

		throw new RuntimeException(
				"Adjacency list with no non-empty map entries encountered.");
	}

	@Override
	public Node getNode(int id) {
		for (Node node : map.keySet())
			if (node.getId() == id)
				return node;

		return null;
	}

	@Override
	public Edge getEdge(int from, int to) {
		
		Node nodeFrom = getNode(from);

		if (!map.containsKey(nodeFrom))
			return null;

		List<AdjacencyDefinition> adjacencyDefinitions = map.get(nodeFrom);

		for (AdjacencyDefinition adjacencyDefinition : adjacencyDefinitions) {
			if (adjacencyDefinition.getDestination() == to)
				return adjacencyDefinition.getEdge();
		}

		return null;
	}

	@Override
	public List<Node> getNodes() {
		Set<Node> result = new HashSet<>();
		for (Map.Entry<Node, List<AdjacencyDefinition>> entry : map.entrySet())
			result.add(entry.getKey());
		return new ArrayList<>(result);
	}

	@Override
	public List<Edge> getEdges() {
		Set<Edge> result = new HashSet<>();
		for (Map.Entry<Node, List<AdjacencyDefinition>> entry : map.entrySet())
			for (AdjacencyDefinition adjacencyDefinition : entry.getValue())
				result.add(adjacencyDefinition.getEdge());
		return new ArrayList<>(result);
	}

	@Override
	public List<Node> getNeighbors(Node from) {
		Set<Node> result = new HashSet<>();
		for (AdjacencyDefinition adjacencyDefinition : map.get(from)) {
			Node node = getNode(adjacencyDefinition.getDestination());
			result.add(node);
		}
		return new ArrayList<>(result);
	}
	
	@Override
	public List<Node> getPredecessors(Node to) {
		Set<Node> result = new HashSet<>();
		for (Map.Entry<Node, List<AdjacencyDefinition>> e : map.entrySet()) {
			for(AdjacencyDefinition ad : e.getValue()) {
				if(ad.getDestination() == to.getId()) {
					result.add(e.getKey());
				}
			}
		}
		return new ArrayList<>(result);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdjacencyList other = (AdjacencyList) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}
}
