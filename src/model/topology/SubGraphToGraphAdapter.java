package model.topology;


import java.util.ArrayList;
import java.util.List;

public class SubGraphToGraphAdapter implements Graph {

	private final SubGraph subGraph;
	private final Graph parent;

	public SubGraphToGraphAdapter(SubGraph subGraph) {
		this.subGraph = subGraph;
		this.parent = subGraph.getParent();
	}

	@Override
	public Graph copy() {
		return new SubGraphToGraphAdapter(subGraph.copy());
	}

	@Override
	public int getNumNodes() {
		return subGraph.getNodes().size();
	}

	@Override
	public int getNumEdges() {
		return subGraph.getEdges().size();
	}

	@Override
	public int getNumMetrics() {
		return parent.getNumMetrics();
	}

	@Override
	public List<Node> getNodes() {
		List<Node> nodes = new ArrayList<>();
		for (Integer node : subGraph.getNodes())
			nodes.add(parent.getNode(node));
		return nodes;
	}

	@Override
	public List<Edge> getEdges() {
		List<Edge> edges = new ArrayList<>();
		for (EdgeDefinition edgeDefinition : subGraph.getEdgeDefinitions())
			edges.add(parent.getEdge(edgeDefinition.getFrom(),
					edgeDefinition.getTo()));
		return edges;
	}

	@Override
	public Node getNode(int node) {
		return parent.getNode(node);
	}

	@Override
	public Edge getEdge(int from, int to) {
		return parent.getEdge(from, to);
	}

	@Override
	public List<Node> getNeighbors(Node from) {
		List<Node> allNeighbors = parent.getNeighbors(from);
		List<Node> subGraphNeighbors = new ArrayList<>();
		for (Node node : allNeighbors) {
			if (!subGraph.containsNode(node.getId())) {
				continue;
			}
			if (!subGraph.containsEdge(from.getId(), node.getId())) {
				continue;
			}
			subGraphNeighbors.add(node);
		}
		return subGraphNeighbors;
	}

	@Override
	public List<Node> getPredecessors(Node to) {
		List<Node> allPredecessors = parent.getPredecessors(to);
		List<Node> subGraphPredecessors = new ArrayList<>();
		for(Node node : allPredecessors) {
			if(!subGraph.containsNode(node.getId())) {
				continue;
			}
			if(!subGraph.containsEdge(node.getId(), to.getId())) {
				continue;
			}
			subGraphPredecessors.add(node);
		}
		return subGraphPredecessors;
	}

}
