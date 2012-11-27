package impossible.tfind.xamcra;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.pfnd.ConstrainedPathFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Tamcra implements ConstrainedPathFinder {

	private final int k;
	private MetricProvider metricProvider;
	private Queue queue;
	private Map<Node, Map<Integer, PathNode>> paths;
	private Map<Node, Map<Integer, List<Double>>> vLengths;
	private Map<Node, Integer> counters;

	public Tamcra(int k) {
		this.k = k;
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		initializeFind(graph, from, constraints);

		// Perform the relaxation.
		while (!queue.isEmpty()) {
			PathNode current = queue.pop();

			if (current.getNode().equals(to)) {
				break;
			}

			for (Node neighbor : graph.getNeighbors(current.getNode())) {
				PathNode previous = current.getPrev();
				if (previous == null || !previous.getNode().equals(neighbor)) {
					analyzeNeighbor(current, neighbor, graph);
				}
			}
		}
		
		// Account for failure.
		if(!paths.containsKey(to)) {
			return null;
		}

		// Build the result.
		int finalK = getCounter(to);
		PathNode finalPNode = paths.get(to).get(finalK);
		Path finalPath = buildPath(finalPNode, graph);

		// Hold the deinitialization until now because the
		// result build process requires some of the 
		// accumulated state.
		deinitializeFind();

		return finalPath;
	}

	private void initializeFind(Graph graph, Node from, List<Double> constraints) {
		metricProvider = new XamcraMetricProvider(constraints);
		queue = new Queue();
		paths = new HashMap<>();
		vLengths = new HashMap<>();
		counters = new HashMap<>();

		for (Node n : graph.getNodes()) {
			setCounter(n, 0);
		}

		List<Double> initialLength = new ArrayList<>();
		for (int i = 0; i < graph.getNumMetrics(); ++i) {
			initialLength.add(0.0);
		}

		setVLength(from, 0, initialLength);
		PathNode pNode = new PathNode(from, 0, null);
		queue.push(0.0, pNode);
	}

	private void deinitializeFind() {
		// Allow GC to take care of these.
		metricProvider = null;
		queue = null;
		paths = null;
		vLengths = null;
		counters = null;
	}

	private void analyzeNeighbor(PathNode current, Node neighbor, Graph graph) {

		// Create the candidate path.
		PathNode newPNode = new PathNode(neighbor, current.getK(), current);
		Path newPath = buildPath(newPNode, graph);

		// Analyze the path.
		double length = metricProvider.getPostAdditive(newPath);
		List<Double> vLength = newPath.getMetrics();

		// Check if the path is dominated
		boolean dominated = false;
		if (paths.containsKey(newPNode.getNode())) {
			for (Map.Entry<Integer, PathNode> entry : paths.get(
					newPNode.getNode()).entrySet()) {
				Path alternative = buildPath(entry.getValue(), graph);
				if (isDominatedBy(vLength, alternative.getMetrics())) {
					dominated = true;
					break;
				}
			}
		}

		// If feasible then process the path.
		if (length > 1) {
			// Path is infeasible.

		} else if (dominated) {
			// Path is dominated.

		} else {

			if (getCounter(neighbor) < k) {

				// It is possible to add yet another
				// path candidate to this node.
				setCounter(neighbor, newPNode.getK() + 1);
				setPath(neighbor, newPNode.getK(), newPNode);
				setVLength(neighbor, newPNode.getK(), vLength);

				queue.push(length, newPNode);

			} else {

				// Can't add more paths. Try replacing
				// the worst path with the new one.
				PathNode oldPNode = queue.findMaxTo(neighbor);
				Path oldPath = buildPath(oldPNode, graph);
				if (length < metricProvider.getPostAdditive(oldPath)) {
					queue.replace(oldPNode, newPNode);
				}
			}
		}
	}

	// Helpers.
	//
	// Package scoped for testing purposes.
	// ------------------------------------

	boolean isDominatedBy(List<Double> aMetrics, List<Double> bMetrics) {
		for (int i = 0; i < aMetrics.size(); ++i) {
			if (bMetrics.get(i) > aMetrics.get(i)) {
				return false;
			}
		}
		return true;
	}

	// Path analysis.
	//
	// Package scoped for testing purposes.
	// ------------------------------------

	Path buildPath(PathNode pNode, Graph graph) {

		List<Integer> nodes = new ArrayList<>();

		while (pNode != null) {
			nodes.add(pNode.getNode().getId());
			pNode = pNode.getPrev();
		}

		return new Path(graph, nodes);
	}

	// State.
	// ------

	// Paths storage.

	private void setPath(Node node, int counter, PathNode path) {
		if (!paths.containsKey(node)) {
			paths.put(node, new TreeMap<Integer, PathNode>());
		}
		paths.get(node).put(counter, path);
	}

	// Length storage.

	private void setVLength(Node node, int i, List<Double> vLength) {
		if (!vLengths.containsKey(node)) {
			vLengths.put(node, new TreeMap<Integer, List<Double>>());
		}
		vLengths.get(node).put(i, vLength);
	}

	// Counter storage.

	private int getCounter(Node node) {
		return counters.get(node);
	}

	private void setCounter(Node node, int counter) {
		counters.put(node, counter);
	}

}
