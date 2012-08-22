package impossible.helpers;

import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.SubGraph;

public class TopologyDebug {

	public String printGraph(Graph graph) {

		StringBuilder result = new StringBuilder();

		result.append("Nodes:\n");
		for (Node node : graph.getNodes()) {
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

	public String printSubGraph(SubGraph subGraph) {

		StringBuilder result = new StringBuilder();
		for (SubGraph.EdgeDefinition edgeDefinition : subGraph
				.getEdgeDefinitions()) {

			result.append(edgeDefinition.getFrom());
			result.append(" - > ");
			result.append(edgeDefinition.getTo());
			result.append('\n');
		}

		return result.toString();
	}
}
