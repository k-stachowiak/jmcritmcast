package edu.ppt.impossible.model;

import java.util.ArrayList;
import java.util.List;

import edu.ppt.impossible.exceptions.IllegalOperationException;
import edu.ppt.impossible.model.SubGraph.EdgeDefinition;

public class SubGraphToGraphAdapter implements Graph {

	private final SubGraph subGraph;
	private final Graph parent;

	public SubGraphToGraphAdapter(SubGraph subGraph) {
		super();
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
		if (!subGraph.containsNode(node))
			throw new IllegalOperationException(
					"Attenpt to get non-existent node from sub-graph");
		return parent.getNode(node);
	}

	@Override
	public Edge getEdge(int from, int to) {
		if (!subGraph.containsEdge(from, to))
			throw new IllegalOperationException(
					"Attenpt to get non-existent edge from sub-graph");
		return parent.getEdge(from, to);
	}

	@Override
	public List<Node> getNeighbors(Node from) {
		List<Node> allNeighbors = parent.getNeighbors(from);
		List<Node> subGraphNeighbors = new ArrayList<>();
		for (Node node : allNeighbors)
			if (subGraph.containsNode(node.getId()))
				subGraphNeighbors.add(node);
		return subGraphNeighbors;
	}

}
