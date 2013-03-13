package edu.put.et.stik.mm.tfind.rdp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.put.et.stik.mm.model.topology.Edge;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;

public class CostConvergenceProcess {

	// External dependencies.
	private final Graph graph;
	private final Node source;
	private final Map<Node, Double> costMap;
	private final Map<Node, Node> predecessorMap;
	private final Set<Node> open;
	private final Set<Node> visited;

	public CostConvergenceProcess(Graph graph, Node source) {

		this.graph = graph;
		this.source = source;
		this.costMap = new HashMap<>();
		this.predecessorMap = new HashMap<>();
		this.open = new HashSet<>();
		this.visited = new HashSet<>();

		// Initialize the maps.
		for (Node n : graph.getNodes()) {
			costMap.put(n, Double.POSITIVE_INFINITY);
			predecessorMap.put(n, n);
		}

		// Initialize the logical procedure.
		costMap.put(source, 0.0);
		open.add(source);
	}

	public double nextEventTime() {
		double min = Double.POSITIVE_INFINITY;
		for (Node node : open) {
			if (costMap.get(node) < min) {
				min = costMap.get(node);
			}
		}
		return min;
	}

	public boolean isDone() {
		return visited.size() == graph.getNumNodes();
	}

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
			if (newCost < costMap.get(neighbor)) {
				costMap.put(neighbor, newCost);
				predecessorMap.put(neighbor, cheapestNode);
				open.add(neighbor);
			}
		}

		// Signal, which node was selected.
		return cheapestNode;
	}
	
	public Path buildPathFrom(Node node) {
		
		List<Integer> nodeIds = new ArrayList<>();
		
		Node current = node;		
		while(current != source) {
			nodeIds.add(current.getId());
			current = predecessorMap.get(current);
		}
		
		nodeIds.add(source.getId());
		
		return new Path(graph, nodeIds);
		
	}
}
