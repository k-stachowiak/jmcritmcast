package edu.ppt.impossible.model;

import java.util.ArrayList;
import java.util.List;

public class SubGraph {

	public static class EdgeDefinition {
		private final int from;
		private final int to;

		public EdgeDefinition(int from, int to) {
			super();
			this.from = from;
			this.to = to;
		}

		public int getFrom() {
			return from;
		}

		public int getTo() {
			return to;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + from;
			result = prime * result + to;
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
			EdgeDefinition other = (EdgeDefinition) obj;
			if (from != other.from)
				return false;
			if (to != other.to)
				return false;
			return true;
		}
	}

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
		return nodes.contains(nodes);
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

	public List<Integer> getNodes() {
		return nodes;
	}

	public List<EdgeDefinition> getEdgeDefinitions() {
		return edgeDefinitions;
	}
}
