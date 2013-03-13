package edu.put.et.stik.mm.model.topology;

import java.util.ArrayList;
import java.util.List;

public class SubGraph {

	protected final Graph parent;
	protected final List<Integer> nodes;
	protected final List<EdgeDefinition> edgeDefinitions;

	public SubGraph(Graph parent, List<Integer> nodes,
			List<EdgeDefinition> edges) {
		this.parent = parent;
		this.nodes = nodes;
		this.edgeDefinitions = edges;
	}

	public Graph getParent() {
		return parent;
	}

	public SubGraph copy() {
		List<Integer> newNodes = new ArrayList<>();
		for (Integer node : nodes)
			newNodes.add(node);

		List<EdgeDefinition> newEdgeDefinitions = new ArrayList<>();
		for (EdgeDefinition edgeDefinition : edgeDefinitions)
			newEdgeDefinitions.add(edgeDefinition);

		return new SubGraph(parent, newNodes, newEdgeDefinitions);
	}

	public boolean containsNode(int node) {
		return nodes.contains(node);
	}

	public List<Integer> getNodes() {
		return nodes;
	}

	public int getNumNodes() {
		return nodes.size();
	}

	public boolean containsEdge(int from, int to) {
		for (EdgeDefinition edgeDefinition : edgeDefinitions) {

			boolean matchDirect = edgeDefinition.getFrom() == from
					&& edgeDefinition.getTo() == to;

			boolean matchReverse = edgeDefinition.getFrom() == to
					&& edgeDefinition.getTo() == from;

			if (matchDirect || matchReverse)
				return true;
		}
		return false;
	}

	public List<EdgeDefinition> getEdgeDefinitions() {
		return edgeDefinitions;
	}

	public List<Edge> getEdges() {
		List<Edge> result = new ArrayList<>();
		for (EdgeDefinition edgeDefinition : edgeDefinitions)
			result.add(parent.getEdge(edgeDefinition.getFrom(),
					edgeDefinition.getTo()));
		return result;
	}

	public int getNumEdges() {
		return edgeDefinitions.size();
	}

	public List<Double> getMetrics() {
		List<Double> result = new ArrayList<>();
		for (EdgeDefinition edgeDefinition : edgeDefinitions) {
			Edge edge = parent.getEdge(edgeDefinition.getFrom(),
					edgeDefinition.getTo());

			if (result.isEmpty())
				result.addAll(edge.getMetrics());

			else
				for (int m = 0; m < edge.getMetrics().size(); ++m)
					result.set(m, result.get(m) + edge.getMetrics().get(m));
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (EdgeDefinition edgeDefinition : edgeDefinitions) {
			stringBuilder.append(edgeDefinition.getFrom() + " -> "
					+ edgeDefinition.getTo() + " ");
		}
		return stringBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((edgeDefinitions == null) ? 0 : edgeDefinitions.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());

		// Note that parent isn't taken into account.

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
		SubGraph other = (SubGraph) obj;
		if (edgeDefinitions == null) {
			if (other.edgeDefinitions != null)
				return false;
		} else if (!compareEdgesLists(edgeDefinitions, other.edgeDefinitions))
			return false;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!compareNodesLists(nodes, other.nodes))
			return false;

		// Note that parent isn't taken into account.

		return true;
	}

	private static boolean compareNodesLists(List<Integer> lhs,
			List<Integer> rhs) {

		for (Integer left : lhs) {
			if (!rhs.contains(left))
				return false;
		}
		return true;
	}

	private static boolean compareEdgesLists(List<EdgeDefinition> lhs,
			List<EdgeDefinition> rhs) {

		for (EdgeDefinition left : lhs) {
			boolean containsDirect = rhs.contains(left);
			boolean containsReverse = rhs.contains(new EdgeDefinition(left
					.getTo(), left.getFrom()));

			if (!containsDirect && !containsReverse)
				return false;
		}

		return true;
	}
}
