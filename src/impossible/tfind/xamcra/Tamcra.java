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

	public Tamcra(List<Double> constraints, int k) {
		this.k = k;		
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {
		
		metricProvider = new XamcraMetricProvider(constraints);
		queue = new Queue();
		paths = new HashMap<>();
		vLengths = new HashMap<>();
		counters = new HashMap<>();

		Path result = null;

		for (Node n : graph.getNodes()) {
			setCounter(n, 0);
		}

		List<Double> initialLength = new ArrayList<>();
		for (int i = 0; i < graph.getNumMetrics(); ++i) {
			initialLength.add(0.0);
		}

		setVLength(from, 1, initialLength);
		PathNode pNode = new PathNode(from, 0, null);
		pNode.setLabel(0.0);
		queue.push(pNode);

		while (!queue.queueEmpty()) {

			PathNode ui = queue.pop();

			if (ui.node.equals(to)) {
				return result;
			}

			for (Node v : graph.getNeighbors(ui.node)) {
				if (ui.prev != null && !ui.prev.node.equals(v)) {
					
					// Create the candidate path.
					PathNode path = new PathNode(v, ui.k + 1, ui);
					
					// Analyze the path.
					double length = computeLength(path, graph);
					path.setLabel(length);
					List<Double> vLength = metrics(path, graph);
					boolean dominated = isDominated(path, graph);
					
					// If feasible then process the path.
					if (length <= 1 && !dominated) {
						
						if (getCounter(v) < k) {
							
							// It is possible to add yet another
							// path candidate to this node.							
							setCounter(v, path.k);
							setPath(v, path.k, path);
							setVLength(v, path.k, vLength);
							
							queue.push(path);
							
						} else {
							
							// Can't add more paths. Try replacing
							// the worst path with the new one.
							PathNode oldPath = queue.popMaxTo(v);
							if (length < computeLength(oldPath, graph)) {
								queue.queueReplace(oldPath, path);
							}
						}
					}
				}
			}
		}
		
		// Allow GC to take care of these.
		metricProvider = null;
		queue = null;
		paths = null;
		vLengths = null;
		counters = null;

		return null;
	}

	// Helpers.
	//
	// Package scoped for testing purposes.
	// ------------------------------------

	double computeLength(PathNode pNode, Graph graph) {
		Path path = buildPath(pNode, graph);
		return metricProvider.getPostAdditive(path);
	}

	boolean isDominated(PathNode path, Graph graph) {
		for (Map.Entry<Integer, PathNode> entry : paths.get(path.node)
				.entrySet()) {
			if (isDominatedBy(path, entry.getValue(), graph)) {
				return true;
			}
		}
		return false;
	}

	boolean isDominatedBy(PathNode a, PathNode b, Graph graph) {
		List<Double> aMetrics = metrics(a, graph);
		List<Double> bMetrics = metrics(b, graph);
		for (int i = 0; i < aMetrics.size(); ++i) {
			if (bMetrics.get(i) < aMetrics.get(i)) {
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
			nodes.add(pNode.node.getId());
			pNode = pNode.prev;
		}		

		return new Path(graph, nodes);
	}

	List<Double> metrics(PathNode pNode, Graph graph) {
		Path path = buildPath(pNode, graph);
		return path.getMetrics();
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
