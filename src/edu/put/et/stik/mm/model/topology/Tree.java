package edu.put.et.stik.mm.model.topology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Tree extends SubGraph {

	public Tree(Graph parent, List<EdgeDefinition> edges) {
		super(parent, deriveNodesFromEdges(edges), edges);
	}

	private static List<Integer> deriveNodesFromEdges(List<EdgeDefinition> edges) {
		Set<Integer> resultSet = new HashSet<>();
		for (EdgeDefinition edgeDefinition : edges) {
			resultSet.add(edgeDefinition.getFrom());
			resultSet.add(edgeDefinition.getTo());
		}
		return new ArrayList<>(resultSet);
	}
}
