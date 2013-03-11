package impossible.model.topology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdjacencyMatrix implements Graph {

	final List<Node> nodes;
	final List<Edge> allEdges;
	int numNodes;
	int numEdges;

	AdjacencyMatrix(List<Node> nodes, List<Edge> allEdges, int numNodes,
			int numEdges) {

		this.nodes = nodes;
		this.allEdges = allEdges;
		this.numNodes = numNodes;
		this.numEdges = numEdges;
	}

	@Override
	public Graph copy() {
		List<Node> nodes = new ArrayList<>(this.nodes);
		List<Edge> allEdges = new ArrayList<>(this.allEdges);
		return new AdjacencyMatrix(nodes, allEdges, numNodes, numEdges);
	}

	@Override
	public int getNumNodes() {
		return numNodes;
	}

	@Override
	public int getNumEdges() {
		return numEdges;
	}

	@Override
	public int getNumMetrics() {
		for (Edge e : allEdges) {
			if (e == null) {
				continue;
			}
			return e.getMetrics().size();
		}
		throw new RuntimeException("Empty graph queried for num metrics.");
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public List<Edge> getEdges() {
		Set<Edge> uniqueEdges = new HashSet<>();
		for(Edge e : allEdges) {
			if(e != null) {
				uniqueEdges.add(e);
			}
		}
		return new ArrayList<>(uniqueEdges);
	}

	@Override
	public Node getNode(int node) {
		for(Node n : nodes) {
			if(n.getId() == node) {
				return n;
			}
		}
		throw new RuntimeException("Requested node of nonexistent id.");
	}

	@Override
	public Edge getEdge(int from, int to) {
		return allEdges.get(from * numNodes + to);
	}

	@Override
	public List<Node> getNeighbors(Node from) {
		Set<Node> uniqueNodes = new HashSet<>();
		int fromId = from.getId();
		for(int toId = 0; toId < numNodes; ++toId) {
			if(getEdge(fromId, toId) != null) {
				uniqueNodes.add(getNode(toId));
			}
		}
		return new ArrayList<Node>(uniqueNodes);
	}

	@Override
	public List<Node> getPredecessors(Node to) {
		Set<Node> uniqueNodes = new HashSet<>();
		int toId = to.getId();
		for(int fromId = 0; fromId < numNodes; ++fromId) {
			if(getEdge(fromId, toId) != null) {
				uniqueNodes.add(getNode(fromId));
			}
		}
		return new ArrayList<Node>(uniqueNodes);
	}
}
