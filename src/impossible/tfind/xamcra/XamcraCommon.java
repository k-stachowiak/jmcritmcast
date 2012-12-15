package impossible.tfind.xamcra;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class XamcraCommon {

	private Map<Node, Map<Integer, PathNode>> paths;
	private Map<Node, Map<Integer, List<Double>>> vLengths;
	private Map<Node, Integer> counters;

	public String pathsString() {
		StringBuilder sb = new StringBuilder();
		for (Node n : paths.keySet()) {
			sb.append("Paths to " + n + ":\n");
			for (Map.Entry<Integer, PathNode> entry : paths.get(n).entrySet()) {
				sb.append("\tp[" + entry.getKey() + "] = " + entry.getValue()
						+ "\n");
			}
		}
		return sb.toString();
	}

	public String lengthsString() {
		StringBuilder sb = new StringBuilder();
		for (Node n : vLengths.keySet()) {
			sb.append("Lengths to " + n + ":\n");
			for (Map.Entry<Integer, List<Double>> entry : vLengths.get(n)
					.entrySet()) {
				sb.append("\tl[" + entry.getKey() + "] = ");
				for(Double m : entry.getValue()) {
					sb.append(m + " ");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public String countersString() {
		StringBuilder sb = new StringBuilder();
		for (Node n : paths.keySet()) {
			sb.append("\tcounter[" + n + "] = " + counters.get(n) + "\n");
		}
		return sb.toString();
	}

	// Helpers.
	// --------

	public boolean leadsTo(Node from, PathNode to) {

		if (!paths.containsKey(to)) {
			return false;
		}

		for (PathNode pNode : paths.get(to).values()) {
			PathNode prev = pNode.getPrev();
			if (prev != null && prev.getNode().equals(from)) {
				return true;
			}
		}

		return false;
	}

	public boolean isDominatedBy(List<Double> aMetrics, List<Double> bMetrics) {
		for (int i = 0; i < aMetrics.size(); ++i) {
			if (bMetrics.get(i) > aMetrics.get(i)) {
				return false;
			}
		}
		return true;
	}

	// Path analysis.
	// --------------

	public Path buildPath(PathNode pNode, Graph graph) {

		List<Integer> nodes = new ArrayList<>();

		while (pNode != null) {
			nodes.add(pNode.getNode().getId());
			pNode = pNode.getPrev();
		}

		return new Path(graph, nodes);
	}

	// State.
	// ------

	public void initialize() {
		paths = new HashMap<>();
		vLengths = new HashMap<>();
		counters = new HashMap<>();
	}

	public void deinitialize() {
		paths = null;
		vLengths = null;
		counters = null;
	}

	// Paths storage.

	public void setPath(Node node, int counter, PathNode path) {
		if (!paths.containsKey(node)) {
			paths.put(node, new TreeMap<Integer, PathNode>());
		}
		paths.get(node).put(counter, path);
	}

	public PathNode getPath(Node node, int counter) {
		return paths.get(node).get(counter);
	}

	public Collection<PathNode> getPaths(Node node) {
		return paths.get(node).values();
	}

	public Map<Integer, PathNode> getPathEntries(Node node) {
		return paths.get(node);
	}

	public boolean hasPathTo(Node to) {
		return paths.containsKey(to);
	}

	// Length storage.

	public void setVLength(Node node, int i, List<Double> vLength) {
		if (!vLengths.containsKey(node)) {
			vLengths.put(node, new TreeMap<Integer, List<Double>>());
		}
		vLengths.get(node).put(i, vLength);
	}

	// Counter storage.

	public int getCounter(Node node) {
		return counters.get(node);
	}

	public void setCounter(Node node, int counter) {
		counters.put(node, counter);
	}
}
