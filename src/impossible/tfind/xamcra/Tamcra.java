package impossible.tfind.xamcra;

import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.util.MyLog;

import java.util.ArrayList;
import java.util.List;

public class Tamcra implements ConstrainedPathFinder {

	private final MyLog logger = MyLog.GENERAL_ALGORITHM_FILE;
	private final XamcraCommon xamcra;
	private final int k;
	private MetricProvider metricProvider;
	private Queue queue;

	public Tamcra(int k) {
		this.xamcra = new XamcraCommon();
		this.k = k;
	}
	
	public String stateString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Counters : \n" + xamcra.countersString());
		sb.append("Lengths : \n" + xamcra.lengthsString());
		sb.append("Paths : \n" + xamcra.pathsString());
		sb.append("Queue : \n" + queue.toString());
		return sb.toString();
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		initializeFind(graph, from, constraints);

		// Perform the relaxation.
		while (!queue.isEmpty()) {
			
			logger.trace("************************************************");
			logger.trace("State : \n" + stateString());
			
			PathNode current = queue.pop();
			logger.trace("Popped from queue : " + current);

			if (current.getNode().equals(to)) {
				logger.trace("Current node is the target. Terminating.");
				break;
			}

			for (Node neighbor : graph.getNeighbors(current.getNode())) {
				logger.trace("Considering neighbor : " + neighbor);
				if (!xamcra.leadsTo(neighbor, current)) {
					logger.trace("Neighbor doesn't lead to current node. Analyzing.");
					analyzeNeighbor(current, neighbor, graph);
				} else {
					logger.trace("Neighbor leads to current node. Skipping.");
				}
			}
		}

		// Account for failure.
		if (!xamcra.hasPathTo(to)) {
			logger.trace("No path to target found.");
			return null;
		} else {
			logger.trace("Path to target found.");
		}

		// Build the result.
		int finalK = xamcra.getCounter(to);
		PathNode finalPNode = xamcra.getPath(to, finalK);
		Path finalPath = xamcra.buildPath(finalPNode, graph);

		// Hold the deinitialization until now because the
		// result build process requires some of the
		// accumulated state.
		deinitializeFind();
		
		logger.trace("Returning path : " + finalPath);

		return finalPath;
	}

	private void initializeFind(Graph graph, Node from, List<Double> constraints) {
		xamcra.initialize();
		metricProvider = new XamcraMetricProvider(constraints);
		queue = new Queue();

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
	}

	private void analyzeNeighbor(PathNode current, Node neighbor, Graph graph) {

		int newK = xamcra.getCounter(neighbor) + 1;
		
		// Create the candidate path.
		PathNode newPNode = new PathNode(neighbor, newK, current);
		Path newPath = xamcra.buildPath(newPNode, graph);

		// Analyze the path.
		double length = metricProvider.getPostAdditive(newPath);
		List<Double> vLength = newPath.getMetrics();

		// Check if the path is dominated
		boolean dominated = false;
		if (xamcra.hasPathTo(newPNode.getNode())) {
			for (PathNode pNode : xamcra.getPaths(newPNode.getNode())) {
				Path alternative = xamcra.buildPath(pNode, graph);
				if (xamcra.isDominatedBy(vLength, alternative.getMetrics())) {
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

			if (xamcra.getCounter(neighbor) < k) {

				// It is possible to add yet another
				// path candidate to this node.
				xamcra.setCounter(neighbor, newK);
				xamcra.setPath(neighbor, newK, newPNode);
				xamcra.setVLength(neighbor, newK, vLength);
				queue.push(length, newPNode);

			} else {

				// Can't add more paths. Try replacing
				// the worst path with the new one.
				PathNode oldPNode = queue.findMaxTo(neighbor);
				Path oldPath = xamcra.buildPath(oldPNode, graph);
				if (length < metricProvider.getPostAdditive(oldPath)) {
					queue.replace(oldPNode, newPNode);
				}
			}
		}
	}
}
