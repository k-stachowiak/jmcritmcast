package edu.ppt.impossible.model;

import java.util.ArrayList;
import java.util.List;


public class Path extends SubGraph {

	public Path(Graph parent, List<Integer> nodes) {
		super(parent, nodes, deriveEdgesFromNodes(nodes));
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

	private static List<EdgeDefinition> deriveEdgesFromNodes(List<Integer> nodes) {
		List<EdgeDefinition> result = new ArrayList<>();
		for (int i = 1; i < nodes.size(); ++i)
			result.add(new EdgeDefinition(nodes.get(i - 1), nodes.get(i)));
		return result;
	}
}
