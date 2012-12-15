package impossible.tfind.rdp;

import impossible.helpers.ConstraintsComparer;
import impossible.model.topology.Edge;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;
import impossible.util.MyLog;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

public class RdpTreeFinder implements MetricConstrainedSteinerTreeFinder {

	// Configuration.
	private final MyLog LOG = MyLog.GENERAL_ALGORITHM_FILE;
	private final ConstraintsComparer constraintsComparer;
	private List<Double> constraints;

	// State.
	private Map<Node, Map<Integer, Map<Integer, Double>>> labels;	// [node][signal][metric] -> cost
	private Map<Node, Map<Integer, Boolean>> sigOpen;				// [node][signal] -> isOpen
	private Map<Node, Map<Node, Node>> predecessors;					// [node][source] -> predecessor

	private AbstractQueue<Event> schedule;

	public String printLabels() {
		StringBuilder sb = new StringBuilder();
		for (Node n : labels.keySet()) {
			sb.append("Node : " + n + '\n');
			for (Integer s : labels.get(n).keySet()) {
				sb.append("\tSignal : " + s + '\n');
				for (Map.Entry<Integer, Double> m : labels.get(n).get(s)
						.entrySet()) {
					sb.append("\t\tm[" + m.getKey() + "] = " + m.getValue()
							+ '\n');
				}
			}
		}
		return sb.toString();
	}

	public String printOpenMap() {
		StringBuilder sb = new StringBuilder();
		for (Node n : sigOpen.keySet()) {
			sb.append("Node : " + n + '\n');
			for (Map.Entry<Integer, Boolean> entry : sigOpen.get(n).entrySet()) {
				sb.append("\tSignal : " + entry.getKey() + " is "
						+ entry.getValue() + '\n');
			}
		}
		return sb.toString();
	}

	public String printPredecessors() {
		StringBuilder sb = new StringBuilder();
		for (Node to : predecessors.keySet()) {
			sb.append("Node : " + to + '\n');
			for (Map.Entry<Node, Node> entry : predecessors.get(to).entrySet()) {
				sb.append("\tFrom : " + entry.getKey() + " through "
						+ entry.getValue() + '\n');
			}
		}
		return sb.toString();
	}

	public RdpTreeFinder(ConstraintsComparer constraintsComparer) {
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Labels:\n");
		sb.append(printLabels());
		sb.append("Open map:\n");
		sb.append(printOpenMap());
		sb.append("Predecessors:\n");
		sb.append(printPredecessors());
		return sb.toString();
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {

		this.constraints = new ArrayList<Double>(constraints);
		init(graph, group);

		while (!schedule.isEmpty()) {

			// Pop the soonest event.
			final Event e = schedule.poll();

			final int signalId = e.getSignalId();
			final Node from = graph.getNode(e.getEdge().getFrom());
			final Node to = graph.getNode(e.getEdge().getTo());
			final List<Double> ms = e.getEdge().getMetrics();

			// Skip already visited nodes.
			if (!sigOpen.get(to).get(signalId)) {
				continue;
			}

			// Update labels.
			Map<Integer, Double> newLabels = new TreeMap<>();
			for (int i = 0; i < ms.size(); ++i) {
				double newLabel = labels.get(from).get(signalId).get(i)
						+ ms.get(i);
				newLabels.put(i, newLabel);
			}
			labels.get(to).put(signalId, newLabels);

			// Update predecessors.
			predecessors.get(to).put(group.get(signalId), from);

			// Schedule further arrivals.
			broadcast(graph, signalId, to);

			// Detect RDP.
			if (numClosed(to) == group.size()) {
				LOG.trace(this);
				Tree candidate = buildTree(graph, to, group);
				if (constraintsComparer.fulfilsAll(candidate, group.get(0),
						constraints)) {
					return candidate;
				}
			}
		}

		return null;
	}

	private void init(Graph graph, List<Node> group) {

		// Helpful abbreviations.
		// ----------------------
		final int S = group.size();
		final int M = graph.getNumMetrics();
		final double inf = Double.POSITIVE_INFINITY;

		// Initialize labels.
		// ------------------
		labels = new HashMap<>();
		for (Node n : graph.getNodes()) {
			labels.put(n, new TreeMap<Integer, Map<Integer, Double>>());
			for (int s = 0; s < S; ++s) {
				Map<Integer, Double> infs = new TreeMap<>();
				for (int m = 0; m < M; ++m) {
					infs.put(m, inf);
				}
				labels.get(n).put(s, infs);
			}
		}
		for (Node n : group) {
			for (int s = 0; s < S; ++s) {
				for (int m = 0; m < M; ++m) {
					labels.get(n).get(s).put(m, 0.0);
				}
			}
		}

		// Initialize open flags.
		// ----------------------
		sigOpen = new HashMap<>();
		for (Node n : graph.getNodes()) {
			sigOpen.put(n, new HashMap<Integer, Boolean>());
			for (int s = 0; s < S; ++s) {
				sigOpen.get(n).put(s, true);
			}
		}

		// Schedule initial events.
		// ------------------------
		schedule = new PriorityQueue<>();
		for (int s = 0; s < S; ++s) {
			broadcast(graph, s, group.get(s));
		}

		// Initialize the predecessor map.
		// -------------------------------
		predecessors = new HashMap<>();
		for (Node n : graph.getNodes()) {
			predecessors.put(n, new HashMap<Node, Node>());
			for (Node g : group) {
				predecessors.get(n).put(g, n);
			}
		}
	}

	private void broadcast(Graph graph, int signalId, Node source) {

		List<Node> neighbors = graph.getNeighbors(source);

		// Determine the metrics accumulated so far.
		Map<Integer, Double> metrics = labels.get(source).get(signalId);

		for (Node n : neighbors) {

			// Determine the outgoing edge.
			if (!sigOpen.get(n).get(signalId)) {
				continue;
			}

			final Edge e = graph.getEdge(source.getId(), n.getId());
			final double time = computeExtendedTime(metrics, e);

			LOG.trace("New event at " + time + '\n');
			schedule.add(new Event(signalId, time, e));
		}

		sigOpen.get(source).put(signalId, false);
	}

	private double computeExtendedTime(Map<Integer, Double> soFar, Edge e) {
		final List<Double> ms = new ArrayList<Double>(e.getMetrics());
		final int M = ms.size();
		double sum = 0.0;
		for (int m = 1; m < M; ++m) {
			double numerator = soFar.get(m) + ms.get(m);
			double denominator = constraints.get(m - 1);
			sum += numerator / denominator;
		}
		return sum;
	}

	private int numClosed(Node node) {
		int result = 0;
		for (Map.Entry<Integer, Boolean> entry : sigOpen.get(node).entrySet()) {
			if (!entry.getValue()) {
				++result;
			}
		}
		return result;
	}

	private Tree buildTree(Graph graph, Node rdp, List<Node> group) {

		Set<EdgeDefinition> eds = new HashSet<>();
		for (Node n : group) {

			Node target = n;
			Node current = rdp;
			Node next;

			if (current.equals(target)) {
				break;
			}

			do {
				next = predecessors.get(current).get(target);
				if (next.equals(current)) {
					throw new RuntimeException("Algorithm logic error.");
				}

				eds.add(new EdgeDefinition(next.getId(), current.getId()));
				current = next;

			} while (!next.equals(target));

		}

		return new Tree(graph, new ArrayList<>(eds));
	}
}
