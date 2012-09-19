package impossible.helpers;

import impossible.model.topology.Edge;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.SubGraph;

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
			result.append(' ');
			for (Double m : edge.getMetrics()) {
				result.append(m);
				result.append(' ');
			}
			result.append('\n');
		}

		return result.toString();
	}

	public String printSubGraph(SubGraph subGraph) {

		StringBuilder result = new StringBuilder();
		for (EdgeDefinition edgeDefinition : subGraph.getEdgeDefinitions()) {

			result.append(edgeDefinition.getFrom());
			result.append(" - > ");
			result.append(edgeDefinition.getTo());
			result.append('\n');
		}

		return result.toString();
	}
}
