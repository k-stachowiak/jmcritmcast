package impossible.tfind.xamcra;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MamcraA {

	private final XamcraCommon xamcra;
	private double endValue;
	private MetricProvider metricProvider;
	private Queue queue;
	private Map<PathNode, Color> colorMap;

	public MamcraA() {
		xamcra = new XamcraCommon();
	}

	public List<Path> findPaths(Graph graph, Node from,
			List<Node> destinations, List<Double> constraints) {

		initializeFind(graph, from, constraints);

		// Perform the relaxation.
		while (!queue.isEmpty()) {
			PathNode current = queue.pop();
			colorMap.put(current, Color.GREY);

			if (allPathsFound(destinations)) {
				break;
			}

			for (Node neighbor : graph.getNeighbors(current.getNode())) {
				if (!xamcra.leadsTo(neighbor, current)) {
					analyzeNeighbor(current, neighbor, graph);
				}
			}
		}

		// Account for failure.
		if (!allPathsFound(destinations)) {
			return null;
		}

		// Build the result.
		List<Path> finalPaths = new ArrayList<>();
		for(Node destination : destinations) {
			int finalK = xamcra.getCounter(destination);
			PathNode finalPNode = xamcra.getPath(destination, finalK);
			Path finalPath = xamcra.buildPath(finalPNode, graph);
			finalPaths.add(finalPath);
		}

		// Hold the deinitialization until now because the
		// result build process requires some of the
		// accumulated state.
		deinitializeFind();

		return finalPaths;
	}

	private void initializeFind(Graph graph, Node from, List<Double> constraints) {
		xamcra.initialize();
		metricProvider = new XamcraMetricProvider(constraints);
		queue = new Queue();
		colorMap = new HashMap<>();

		endValue = 1.0;

		for (Node n : graph.getNodes()) {
			xamcra.setCounter(n, 0);
		}

		List<Double> initialLength = new ArrayList<>();
		for (int i = 0; i < graph.getNumMetrics(); ++i) {
			initialLength.add(0.0);
		}

		xamcra.setVLength(from, 0, initialLength);
		PathNode pNode = new PathNode(from, 0, null);
		queue.push(0.0, pNode);
	}

	private void deinitializeFind() {
		// Allow GC to take care of these.
		xamcra.deinitialize();
		metricProvider = null;
		queue = null;
		colorMap = null;
	}

	private void analyzeNeighbor(PathNode current, Node neighbor, Graph graph) {

		int newK = xamcra.getCounter(neighbor) + 1;

		// Create the candidate path.
		PathNode candPNode = new PathNode(neighbor, newK, current);
		Path candPath = xamcra.buildPath(candPNode, graph);

		// Analyze the path.
		double length = metricProvider.getPostAdditive(candPath);
		List<Double> vLength = candPath.getMetrics();

		// Check if the path is dominated
		boolean dominated = false;
		if (xamcra.hasPathTo(candPNode.getNode())) {
			for (PathNode altPNode : xamcra.getPaths(candPNode.getNode())) {

				// Skip black nodes.
				if (colorMap.get(altPNode) == Color.BLACK) {
					continue;
				}

				// Mark black if necessary.
				Path altP = xamcra.buildPath(altPNode, graph);
				double altLength = metricProvider.getPostAdditive(altP);
				if (altLength > endValue) {
					colorMap.put(altPNode, Color.BLACK);
					continue;
				}

				// Check for domination.
				if (xamcra.isDominatedBy(vLength, altP.getMetrics())) {
					dominated = true;
					break;
				}
			}
		}

		// If feasible then process the path.
		if (length > endValue) {
			// Path is infeasible.

		} else if (dominated) {
			// Path is dominated.

		} else {

			PathNode firstBlack = null;
			for (PathNode pNode : queue.findAllTo(neighbor)) {
				if (colorMap.get(pNode) == Color.BLACK) {
					firstBlack = pNode;
					break;
				}
			}

			// No black paths in queue
			if (firstBlack == null) {
				xamcra.setCounter(neighbor, newK);
				xamcra.setPath(neighbor, newK, candPNode);
				xamcra.setVLength(neighbor, newK, vLength);
				queue.push(length, candPNode);
			} else {
				replacePath(firstBlack, candPNode);
			}
		}
	}

	// Helpers.
	//
	// Package scoped for testing purposes.
	// ------------------------------------

	private boolean allPathsFound(List<Node> destinations) {
		for (Node destination : destinations) {
			if (!xamcra.hasPathTo(destination)) {
				return false;
			}
		}
		return true;
	}

	void replacePath(PathNode replaced, PathNode newPath) {
		Map<Integer, PathNode> replCands = xamcra.getPathEntries(replaced
				.getNode());
		if (replCands == null) {
			throw new RuntimeException("Replacement at an "
					+ "invalid node requested.");
		}
		for (Map.Entry<Integer, PathNode> entry : replCands.entrySet()) {
			if (entry.getValue().equals(replaced)) {
				replCands.remove(entry.getKey());
				replCands.put(newPath.getK(), newPath);
				return;
			}
		}
		throw new RuntimeException("Replacement of a "
				+ "non-existent path requested.");
	}
}
