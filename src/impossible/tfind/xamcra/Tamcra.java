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

	private String printPaths() {
		StringBuilder sb = new StringBuilder("Paths:");
		for (Map.Entry<Node, Map<Integer, PathNode>> nodeToKPaths : paths
				.entrySet()) {
			sb.append("Towards node : " + nodeToKPaths.getKey() + "\n");
			for (Map.Entry<Integer, PathNode> kToPath : nodeToKPaths.getValue()
					.entrySet()) {
				sb.append("Path no : " + kToPath.getKey() + " : "
						+ kToPath.getValue());
			}
		}
		return sb.toString();
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		initializeFind(graph, from, constraints);

		// Perform the relaxation.
		while (!queue.isEmpty()) {

			System.out.println("Queue : \n" + queue);
			PathNode current = queue.pop();
			System.out.println("Popped : " + current);

			if (current.getNode().equals(to)) {
				System.out.println("Reached target. Aborting.");
				break;
			}

			for (Node neighbor : graph.getNeighbors(current.getNode())) {

				System.out.println("Analyzing neighbor : " + neighbor);
				PathNode previous = current.getPrev();
				System.out.println("Alternative predecessor : " + previous);

				if (previous != null && !previous.getNode().equals(neighbor)) {
					analyzeNeighbor(current, neighbor, graph);
				} else {
					System.out.println("Won't analyze neighbor.");
				}
			}

			System.out.println("End of the iteration. Paths : \n"
					+ printPaths());
		}

		System.out.println("Done iterating.");

		// Build the result.
		Path result = null;
		for (Map.Entry<Integer, PathNode> entry : paths.get(to).entrySet()) {
			if (result != null) {
				throw new RuntimeException(
						"Multiple paths found at the destination.");
			}
			result = buildPath(entry.getValue(), graph);
		}

		deinitializeFind();

		return result;
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

		setVLength(from, 1, initialLength);
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

		System.out.println("Analyzing neighbor : " + neighbor);

		// Create the candidate path.
		PathNode newPNode = new PathNode(neighbor, current.getK() + 1, current);
		Path newPath = buildPath(newPNode, graph);

		System.out.println("Proposed path : " + newPath);

		// Analyze the path.
		double length = metricProvider.getPostAdditive(newPath);
		List<Double> vLength = newPath.getMetrics();

		System.out.println("Path's length : " + length);
		System.out.print("Path's particular costs : ");
		for (Double cost : vLength) {
			System.out.print(cost + " ");
		}
		System.out.println();

		// Check if the path is dominated
		boolean dominated = false;
		for (Map.Entry<Integer, PathNode> entry : paths.get(newPNode.getNode())
				.entrySet()) {
			Path alternative = buildPath(entry.getValue(), graph);
			System.out.print("Is dominated by : " + alternative.toString()
					+ "...");
			if (isDominatedBy(vLength, alternative.getMetrics())) {
				dominated = true;
				System.out.println("YES");
				break;
			} else {
				System.out.println("NO");
			}
		}

		// If feasible then process the path.
		if (length > 1) {
			System.out.println("Path is infeasible.");

		} else if (dominated) {
			System.out.println("Path is dominated.");

		} else {

			System.out.println("Path is feasible and not dominated.");

			if (getCounter(neighbor) < k) {

				// It is possible to add yet another
				// path candidate to this node.
				setCounter(neighbor, newPNode.getK());
				setPath(neighbor, newPNode.getK(), newPNode);
				setVLength(neighbor, newPNode.getK(), vLength);

				System.out.println("Path candidate can be added at this node.");

				queue.push(length, newPNode);

			} else {

				// Can't add more paths. Try replacing
				// the worst path with the new one.
				PathNode oldPNode = queue.findMaxTo(neighbor);
				Path oldPath = buildPath(oldPNode, graph);
				System.out.println("Can't add more paths.");
				if (length < metricProvider.getPostAdditive(oldPath)) {
					System.out.println("...but allowed to replace " + oldPath
							+ ".");
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
