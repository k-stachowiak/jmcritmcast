package model.topology;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrixFactory extends GraphFactory {

	@Override
	public Graph createFromLists(List<Node> nodes, List<Edge> edges) {
		
		final List<Node> nodesCopy = new ArrayList<>(nodes);
		final List<Edge> allEdges = new ArrayList<>();
		final int numNodes = nodes.size();
		final int numEdges = edges.size();
		
		final double sizeSqr = edges.size() * edges.size();
		
		for (int i = 0; i < sizeSqr; ++i) {
			allEdges.add(null);
		}
		
		for (Edge e : edges) {
			int from = e.getFrom();
			int to = e.getTo();
			allEdges.set(from * numNodes + to, e);
			allEdges.set(to * numNodes + from, e);
		}
		
		return new AdjacencyMatrix(nodesCopy, allEdges, numNodes, numEdges);
	}

}
