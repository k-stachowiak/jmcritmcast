package edu.ppt.impossible.helpers;

import edu.ppt.impossible.model.Edge;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.Node;

public class GraphDebug {

	public String printGraph(Graph graph) {
		
		StringBuilder result = new StringBuilder();
		
		result.append("Nodes:\n");
		for(Node node : graph.getNodes()) {
			result.append(node.getId());
			result.append(' ');
		}
		result.append('\n');
		
		result.append("Edges:\n");
		for (Edge edge : graph.getEdges()) {
			result.append(edge.getFrom());
			result.append(" -> ");
			result.append(edge.getTo());
			result.append('\n');
		}
		
		return result.toString();
	}

}
