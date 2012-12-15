package impossible.tfind.rdp.newimpl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;

public class CostConvergenceProcess implements ConvergenceProcess {

	// External dependencies.
	private final Graph graph;
	private final Map<Node, Double> costMap;
	private final Map<Node, Node> predecessorMap;

	// State.
	private Set<Node> open;
	private Set<Node> visited;

	public CostConvergenceProcess(Graph graph, Map<Node, Double> costMap,
			Map<Node, Node> predecessorMap, Node source) {

		this.graph = graph;
		this.costMap = costMap;
		this.predecessorMap = predecessorMap;

		// Take over the maps.
		costMap.clear();
		predecessorMap.clear();

		// Initialize the maps.
		for (Node n : graph.getNodes()) {
			costMap.put(n, Double.POSITIVE_INFINITY);
			predecessorMap.put(n, n);
		}

		// Initialize the state.
		open = new HashSet<>();
		visited = new HashSet<>();

		// Initialize the logical procedure.
		costMap.put(source, 0.0);
		open.add(source);
	}

	@Override
	public double nextEventTime() {
		double min = Double.POSITIVE_INFINITY;
		for(Node node : open) {
			if(costMap.get(node) < min) {
				min = costMap.get(node);
			}
		}
		return min;
	}

	@Override
	public boolean isDone() {
		return visited.size() == graph.getNumNodes();
	}

	@Override
	public Node handleNextEvent() {

		// Find cheapest open.
		double cheapestCost = Double.POSITIVE_INFINITY;
		Node cheapestNode = null;
		for (Node n : open) {
			double cost = costMap.get(n);
			if (cost < cheapestCost) {
				cheapestCost = cost;
				cheapestNode = n;
			}
		}

		// Manipulate the relevant sets.
		visited.add(cheapestNode);
		open.remove(cheapestNode);

		// Analyze the cheapest's neighbors.
		for (Node neighbor : graph.getNeighbors(cheapestNode)) {
			
			// Skip visited.
			if (visited.contains(neighbor)) {
				continue;
			}
			
			// Compute candidate cost.
			Edge edge = graph.getEdge(cheapestNode.getId(), neighbor.getId());
			double newCost = cheapestCost + edge.getMetrics().get(0);
			
			// Replace if cheaper.
			if(newCost < costMap.get(neighbor)) {
				costMap.put(neighbor, newCost);
				predecessorMap.put(neighbor, cheapestNode);
				open.add(neighbor);
			}
		}
		
		// Signal, which node was selected.
		return cheapestNode;
	}
}
