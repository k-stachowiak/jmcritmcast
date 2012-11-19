package impossible.kpfnd;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.SubGraph;
import impossible.pfnd.CommonRelaxation;
import impossible.pfnd.CommonRelaxationImpl;
import impossible.pfnd.PathFinder;
import impossible.pfnd.dkstr.DijkstraPathFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JimenezMarzalKsp {

	class Path {
		public final Node node;
		public final int k;
		public final Path prev;

		public Path(Node node, int k, Path prev) {
			this.node = node;
			this.k = k;
			this.prev = prev;
		}
	}

	// Configuration.
	private final MetricProvider metricProvider;
	private Graph graph;
	private Node source;

	// State.
	private Map<Node, List<Path>> paths;

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

		Node anyNode = graph.getNodes().get(0);
		Node anyOtherNode = graph.getNodes().get(1);
		pathFinder.find(graph, anyNode, anyOtherNode);

		// Fill the state structure with the preliminary predecessor
		// information.
		for (Node n : graph.getNodes()) {
			initializePathRec(n, commonRelaxation.getPredecessors());
		}
	}

	private void initializePathRec(Node node, Map<Node, Node> predecessors) {

		// Handle the path key.
		if (paths.containsKey(node)) {
			return;
		} else {
			paths.put(node, new ArrayList<Path>());
		}

		// Handle the path value.
		Path path = null;
		Node pred = predecessors.get(node);
		if (pred == node) {
			path = new Path(node, 0, null);
		} else {
			if (!paths.containsKey(pred)) {
				initializePathRec(pred, predecessors);
			} else if (paths.get(pred).isEmpty()) {
				throw new RuntimeException(
						"Cycle found in the Dijkstra's predecessors tree");
			}
			path = new Path(node, 0, paths.get(pred).get(0));
		}

		paths.get(node).add(path);
	}

	public void computePath(Node destination, int k) {
		while (paths.get(destination).size() <= k) {
			nextPath(destination, paths.get(destination).size());
		}
	}

	private void nextPath(Node destination, int k) {

		List<Path> candidates = getCandidates(destination, k);

		if (candidates.isEmpty()) {
			paths.get(destination).add(null);
			return;
		}

		double cheapestCost = Double.POSITIVE_INFINITY;
		Path cheapestPath = null;
		for (Path candidate : candidates) {
			double candidateCost = costOf(candidate);
			if (candidateCost < cheapestCost) {
				cheapestCost = candidateCost;
				cheapestPath = candidate;
			}
		}

		paths.get(destination).add(cheapestPath);
	}

	private double costOf(Path path) {		
		List<Integer> nodes = new ArrayList<>();
		nodes.add(path.node.getId());
		while (!path.node.equals(source)) {
			path = path.prev;
			nodes.add(path.node.getId());
		}
		SubGraph sg = new impossible.model.topology.Path(graph, nodes);
		return metricProvider.getPreAdditive(sg);
	}

	private List<Path> getCandidates(Node destination, int k) {

		List<Path> candidates = new ArrayList<>();

		// Initial case.
		if (k == 1) {
			Path path0 = paths.get(destination).get(0);
			for (Node predecessor : graph.getPredecessors(destination)) {
				Path fromPred = paths.get(predecessor).get(0);
				fromPred = new Path(destination, k, fromPred);
				if (!pathsEqual(path0, fromPred)) {
					candidates.add(fromPred);
				}
			}

			if (destination.equals(source)) {
				return candidates;
			}
		}

		// General case
		Path prevPath = paths.get(destination).get(k - 1).prev;
		Node u = prevPath.node;
		int kp = prevPath.k;

		if (paths.get(u).size() >= (kp + 1)) {
			nextPath(u, kp + 1);
		}

		prevPath = paths.get(u).get(kp + 1);
		if (paths.get(u).get(kp + 1) != null) {
			Path newPath = new Path(destination, k, prevPath);
			candidates.add(newPath);
		}

		return candidates;
	}

	private boolean pathsEqual(Path a, Path b) {
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
