package helpers.gphmut;

import java.util.ArrayList;
import java.util.List;

import model.topology.Edge;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.Node;
import model.topology.SubGraph;



public class NodeRemover {

	public SubGraph removeNodes(Graph graph, List<Node> toRemove) {

		// Copy the topology data.
		List<Integer> nodes = new ArrayList<>();
		for (Node n : graph.getNodes()) {
			nodes.add(n.getId());
		}

		List<EdgeDefinition> edges = new ArrayList<>();
		for (Edge e : graph.getEdges()) {
			edges.add(new EdgeDefinition(e.getFrom(), e.getTo()));		
		}

		// Remove the given nodes and adjacent edges.
		for (Node n : toRemove) {
			nodes.remove(n);
			List<EdgeDefinition> obsoleteEdges = new ArrayList<>();
			for (EdgeDefinition e : edges) {
				if (e.getFrom() == n.getId() || e.getTo() == n.getId()) {
					obsoleteEdges.add(e);
				}
			}
			edges.removeAll(obsoleteEdges);
		}

		// Build the result topology.
		return new SubGraph(graph, nodes, edges);
	}
}
