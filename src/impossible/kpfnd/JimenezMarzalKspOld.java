package impossible.kpfnd;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JimenezMarzalKspOld {

	// Configuration.
	private Graph graph;
	private Node source;

	// State.
	private Map<Integer, Map<Node, List<Integer>>> paths;

	public void reset(Graph graph, Node source) {
		this.graph = graph;
		this.source = source;
		paths = new HashMap<>();

		// # A.1
		initializePaths();
	}

	public Path getPath(int k, Node destination) {

		// # A.2, A.2.1
		for (int i = 0; i <= k; ++i) {
			if (!paths.containsKey(i)) {
				computePath(destination, i);
			}
		}

		return new Path(graph, paths.get(k).get(destination));
	}

	private void initializePaths() {

		// Find the paths.
		// ---------------

		// State.
		Map<Node, Node> predecessors = new HashMap<>();
		Map<Node, Double> costs = new HashMap<>();
		Set<Node> q = new HashSet<>();

		// Initialize.
		for (Node n : graph.getNodes()) {
			predecessors.put(n, n);
			costs.put(n, Double.POSITIVE_INFINITY);
			q.add(n);
		}
		costs.put(source, 0.0);

		// Iterate.
		while (!q.isEmpty()) {

			// Find cheapest open node.
			double cheapestCost = Double.POSITIVE_INFINITY;
			Node cheapestNode = null;
			for (Node n : q) {
				double cost = costs.get(n);
				if (cost < cheapestCost) {
					cheapestCost = cost;
					cheapestNode = n;
				}
			}
			q.remove(cheapestNode);

			// Relax neighbors.
			for (Node n : graph.getNeighbors(cheapestNode)) {
				Edge e = graph.getEdge(cheapestNode.getId(), n.getId());
				double candidateCost = costs.get(cheapestNode)
						+ e.getMetrics().get(0);
				if (candidateCost < costs.get(n)) {
					costs.put(n, candidateCost);
					predecessors.put(n, cheapestNode);
				}
			}
		}

		// Fill the paths entry.
		// ---------------------
		Map<Node, List<Integer>> pathMap = new HashMap<>();
		for (Node n : graph.getNodes()) {
			List<Integer> nodes = new ArrayList<>();
			Node currentNode = n;
			nodes.add(currentNode.getId());
			do {
				currentNode = predecessors.get(currentNode);
				nodes.add(currentNode.getId());
			} while (currentNode != source);
			Collections.reverse(nodes);
			pathMap.put(n, nodes);
		}

		paths.put(0, pathMap);
	}

	private void computePath(Node destination, int k) {

		// # B.1
		List<List<Integer>> candidates = (k == 1) ? candidatesK1(destination)
				: (new ArrayList<List<Integer>>());

		// # B.2
		if (k > 1 || !destination.equals(source)) {

			// # B.3
			List<Integer> previous = new ArrayList<Integer>(paths.get(k - 1)
					.get(destination));
			for (int kp = 0; kp < (k - 1); ++k) {
				for (Node n : graph.getPredecessors(destination)) {
					List<Integer> valid = paths.get(kp).get(n);
					valid.add(destination.getId());
					if (valid.equals(previous)) {

						// # B.4
						computePath(n, kp);
						List<Integer> candidate = paths.get(kp).get(n);

						// # B.5
						if (candidate != null) {
							candidates.add(candidate);
						}
					}
				}
			}
		}
		
		if(!paths.containsKey(k)) {
			paths.put(k, new HashMap<Node, List<Integer>>());
		}
		
		System.out.print("Looking for " + k + "-th path to " + destination.getId() + "...");

		// # B.6
		if (candidates.isEmpty()) {
			paths.get(k).put(destination, null);
			System.out.println("failure.");
		} else {
			double cheapestCost = Double.POSITIVE_INFINITY;
			List<Integer> cheapestPath = null;
			for (List<Integer> c : candidates) {
				double cost = 0.0;
				for (int i = 1; i < c.size(); ++i) {
					Edge e = graph.getEdge(c.get(i - 1), c.get(i));
					cost += e.getMetrics().get(0);
				}
				if (cost < cheapestCost) {
					cheapestCost = cost;
					cheapestPath = c;
				}
			}
			paths.get(k).put(destination, cheapestPath);
			System.out.println("success.");
		}
	}

	private List<List<Integer>> candidatesK1(Node destination) {
		List<List<Integer>> candidates = new ArrayList<>();
		List<Integer> prevPath = paths.get(0).get(destination);
		for (Node pred : graph.getPredecessors(destination)) {
			List<Integer> candidate = new ArrayList<>(paths.get(0).get(pred));
			candidate.add(destination.getId());
			if (!candidate.equals(prevPath)) {
				candidates.add(candidate);
			}
		}
		return candidates;
	}
}
