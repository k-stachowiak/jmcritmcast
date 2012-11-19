package impossible.kpfnd;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.model.topology.SubGraph;
import impossible.pfnd.CommonRelaxation;
import impossible.pfnd.CommonRelaxationImpl;
import impossible.pfnd.PathFinder;
import impossible.pfnd.dkstr.DijkstraPathFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JimenezMarzalKsp {

	class PathNode {
		public final Node node;
		public final int k;
		public final PathNode prev;

		public PathNode(Node node, int k, PathNode prev) {
			this.node = node;
			this.k = k;
			this.prev = prev;
		}
	}

	private void printState() {
		System.out.println("k = " + currentK());
		for (Map.Entry<Node, List<PathNode>> entry : paths.entrySet()) {
			System.out.println(entry.getKey().getId() + ":");
			for (PathNode pathNode : entry.getValue()) {
				if(pathNode == null) {
					System.out.println("null");
				} else {				
					PathNode prev = pathNode.prev;
					System.out.println(pathNode.node.getId()
							+ "("
							+ pathNode.k
							+ ")"
							+ " -> "
							+ ((prev == null) ? "null" : (prev.node.getId() + "("
									+ prev.k + ")")));
				}
			}
		}
	}

	private int currentK() {
		int maxK = -1;
		for(Map.Entry<Node, List<PathNode>> entry : paths.entrySet()) {
			int k = entry.getValue().size() - 1;
			if(k > maxK) {
				maxK = k;
			}
		}
		return maxK;
	}

	// Configuration.
	private final MetricProvider metricProvider;
	private Graph graph;
	private Node source;

	// State.
	private Map<Node, List<PathNode>> paths;

	public JimenezMarzalKsp(MetricProvider metricProvider) {
		this.metricProvider = metricProvider;
		graph = null;
		source = null;
	}

	public void initialize(Graph graph, Node source) {

		// Initialize the configuration references.
		this.graph = graph;
		this.source = source;
		paths = new HashMap<>();

		// Prepare an initial predecessors map.
		CommonRelaxation commonRelaxation = new CommonRelaxationImpl(
				metricProvider);

		PathFinder pathFinder = new DijkstraPathFinder(commonRelaxation);

		Node notSource = null;
		for (Node n : graph.getNodes()) {
			if (!n.equals(source)) {
				notSource = n;
				break;
			}
		}

		pathFinder.find(graph, source, notSource);

		// Fill the state structure with the preliminary predecessor
		// information.
		for (Node n : graph.getNodes()) {
			initializePathRec(n, commonRelaxation.getPredecessors());
		}

		printState();
	}

	private void initializePathRec(Node node, Map<Node, Node> predecessors) {

		// Handle the path key.
		if (paths.containsKey(node)) {
			return;
		} else {
			paths.put(node, new ArrayList<PathNode>());
		}

		// Handle the path value.
		PathNode path = null;
		Node pred = predecessors.get(node);
		if (pred == node) {
			path = new PathNode(node, 0, null);
		} else {
			if (!paths.containsKey(pred)) {
				initializePathRec(pred, predecessors);
			} else if (paths.get(pred).isEmpty()) {
				throw new RuntimeException(
						"Cycle found in the Dijkstra's predecessors tree");
			}
			path = new PathNode(node, 0, paths.get(pred).get(0));
		}

		paths.get(node).add(path);
	}

	public Path getPath(Node destination, int k) {
		while (paths.get(destination).size() <= k) {
			nextPath(destination, paths.get(destination).size());			
		}
		printState();
		return toModelPath(paths.get(destination).get(k));
	}

	private Path toModelPath(PathNode path) {
		List<Integer> ids = new ArrayList<>();
		ids.add(path.node.getId());
		do {
			path = path.prev;
			ids.add(path.node.getId());
		} while (path.prev != null);
		return new Path(graph, ids);
	}

	private void nextPath(Node destination, int k) {

		List<PathNode> candidates = getCandidates(destination, k);

		if (candidates.isEmpty()) {
			paths.get(destination).add(null);
			return;
		}

		double cheapestCost = Double.POSITIVE_INFINITY;
		PathNode cheapestPath = null;
		for (PathNode candidate : candidates) {
			double candidateCost = costOf(candidate);
			if (candidateCost < cheapestCost) {
				cheapestCost = candidateCost;
				cheapestPath = candidate;
			}
		}

		paths.get(destination).add(cheapestPath);
	}

	private double costOf(PathNode path) {
		List<Integer> nodes = new ArrayList<>();
		nodes.add(path.node.getId());
		while (!path.node.equals(source)) {
			path = path.prev;
			nodes.add(path.node.getId());
		}
		SubGraph sg = new Path(graph, nodes);
		return metricProvider.getPreAdditive(sg);
	}

	private List<PathNode> getCandidates(Node destination, int k) {

		List<PathNode> candidates = new ArrayList<>();

		// Initial case.
		if (k == 1) {
			PathNode path0 = paths.get(destination).get(0);
			for (Node predecessor : graph.getPredecessors(destination)) {
				PathNode fromPred = paths.get(predecessor).get(0);
				fromPred = new PathNode(destination, k, fromPred);
				if (!pathsEqual(path0, fromPred)) {
					candidates.add(fromPred);
				}
			}

			if (destination.equals(source)) {
				return candidates;
			}
		}

		// General case
		PathNode prevPath = paths.get(destination).get(k - 1).prev;
		Node u = prevPath.node;
		int kp = prevPath.k;

		if (paths.get(u).size() >= (kp + 1)) {
			nextPath(u, kp + 1);
		}

		prevPath = paths.get(u).get(kp + 1);
		if (paths.get(u).get(kp + 1) != null) {
			PathNode newPath = new PathNode(destination, k, prevPath);
			candidates.add(newPath);
		}

		return candidates;
	}

	private boolean pathsEqual(PathNode a, PathNode b) {
		while (a != null && b != null) {
			if (!a.node.equals(b.node)) {
				return false;
			}
			a = a.prev;
			b = b.prev;
		}
		return true;
	}
}
