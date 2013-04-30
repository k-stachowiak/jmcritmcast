package tfind.rdp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;


public class HeurNlConvergenceProcess {

	// External dependencies.
	private final Graph graph;
	private final Node source;
	private final List<Double> constraints;
	private final Map<Node, Double> lenMap;
	private final Map<Node, List<Double>> lenVMap;
	private final Map<Node, Node> predMap;

	// State.
	private Set<Node> open;
	private Set<Node> visited;

	public HeurNlConvergenceProcess(Graph graph, List<Double> constraints, Node source) {

		// Initialize the dependencies.
		this.graph = graph;
		this.source = source;
		this.constraints = constraints;
		lenMap = new HashMap<>();
		lenVMap = new HashMap<>();
		predMap = new HashMap<>();
		open = new HashSet<>();
		visited = new HashSet<>();

		// Initialize the maps.
		for (Node n : graph.getNodes()) {
			lenMap.put(n, Double.POSITIVE_INFINITY);
			predMap.put(n, n);
			// The length vectors aren't set here as they're
			// not going to be read before they have got written.
		}

		// Initialize the logical procedure.
		List<Double> zeroV = new ArrayList<>();
		for (int i = 0; i < graph.getNumMetrics(); ++i) {
			zeroV.add(0.0);
		}

		lenMap.put(source, 0.0);
		lenVMap.put(source, zeroV);

		open.add(source);
	}

	public double nextEventTime() {
		double min = Double.POSITIVE_INFINITY;
		for (Node node : open) {
			if (lenMap.get(node) < min) {
				min = lenMap.get(node);
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
			double cost = lenMap.get(n);
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

			// Check if the relaxation is needed.
			double newLen = computeLenCandidate(cheapestNode, neighbor);
			if (newLen < lenMap.get(neighbor)) {
				
				Edge edge = graph.getEdge(cheapestNode.getId(),
						neighbor.getId());
				
				// Update aggregated length.
				lenMap.put(neighbor, newLen);
				
				// Update the individual lengths.
				List<Double> newMetrics = new ArrayList<>();
				for (int m = 0; m < graph.getNumMetrics(); ++m) {
					double newMetric = lenVMap.get(cheapestNode).get(m)
							+ edge.getMetrics().get(m);
					newMetrics.add(newMetric);
				}
				lenVMap.put(neighbor, newMetrics);
				
				// Update the predecessors' map.
				predMap.put(neighbor, cheapestNode);
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
			current = predMap.get(current);
		}
		
		nodeIds.add(source.getId());
		
		return new Path(graph, nodeIds);
		
	}

	private double computeLenCandidate(Node from, Node to) {

		Edge edge = graph.getEdge(from.getId(), to.getId());

		double max = Double.NEGATIVE_INFINITY;
		for (int m = 0; m < constraints.size(); ++m) {

			// The relevant constraint.
			double c = constraints.get(m);
			
			// The relevant cost to the edge source.
			double r = lenVMap.get(from).get(m + 1);
			
			// The relevant edge metric.
			double w = edge.getMetrics().get(m + 1);

			double candidate = (r + w) / c;
			if (candidate > max) {
				max = candidate;
			}
		}

		return max;
	}

}
