package impossible.tfind.rdp;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import impossible.model.topology.Edge;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;

public class RdpTreeFinder implements MetricConstrainedSteinerTreeFinder {
	
	// Configuration.
	private List<Double> constraints;
	
	// State.
	private Map<Node, Map<Integer, Double>> labels;		// [node][metric]
	private Map<Node, Map<Integer, Boolean>> sigOpen;	// [node][signal]
	private Map<Node, Map<Node, Node>> predecessors;	// [node][predecessor]
	
	private AbstractQueue<Event> schedule;
	
	private double simTime;

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {
		
		this.constraints = new ArrayList<Double>(constraints);
		init(graph, group);
		
		while(!schedule.isEmpty()) {
			
			// Pop the soonest event.
			// ----------------------
			final Event e = schedule.poll();
			
			final int signalId = e.getSignalId();
			final Node from = graph.getNode(e.getEdge().getFrom());
			final Node to = graph.getNode(e.getEdge().getTo());
			final List<Double> ms = e.getEdge().getMetrics();
			
			// Advance the simulation time.
			// ----------------------------
			simTime = e.getTime();
			
			// Skip already visited nodes.
			// ---------------------------
			if(!sigOpen.get(to).get(signalId)) {
				continue;
			}
			
			// Update the destination.
			// -----------------------
			
			// Update labels.
			for(int i = 0; i < ms.size(); ++i) {
				double newLabel = labels.get(to).get(i) + ms.get(i);
				labels.get(to).put(i, newLabel);
			}
			
			// Update predecessors.
			predecessors.get(to).put(group.get(signalId), from);
			
			// Schedule further arrivals.
			// --------------------------
			broadcast(graph, signalId, to);
			
			// Detect RDP.
			// -----------
			if(numClosed(to) == group.size()) {
				Tree candidate = tryBuildingTree(graph, to, group);
				if(candidate != null)
					return candidate;
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
		for(Node n : graph.getNodes()) {
			labels.put(n, new HashMap<Integer, Double>());
			for(int m = 0; m < M; ++m) {
				labels.get(n).put(m, inf);
			}				
		}		
		for(Node n : group) {
			for(int m = 0; m < M; ++m) {
				labels.get(n).put(m, 0.0);
			}				
		}
		
		// Initialize open flags.
		// ----------------------
		sigOpen = new HashMap<>();
		for(Node n : graph.getNodes()) {
			sigOpen.put(n, new HashMap<Integer, Boolean>());
			for(int s = 0; s < S; ++s) {
				sigOpen.get(n).put(s, true);
			}				
		}
		
		// Schedule initial events.
		// ------------------------
		schedule = new PriorityQueue<>();
		for(int s = 0; s < S; ++s) {
			broadcast(graph, s, group.get(s));
		}
		
		// Initialize the predecessor map.
		// -------------------------------
		predecessors = new HashMap<>();
		for(Node n : graph.getNodes()) {
			predecessors.put(n, new HashMap<Node, Node>());
			for(Node g : group) {
				predecessors.get(n).put(g, n);
			}				
		}
		
		// Initialize the simulation time.
		// ------------------------------
		simTime = 0.0;
	}

	private void broadcast(Graph graph, int signalId, Node source) {
		
		List<Node> neighbors = graph.getNeighbors(source);
		for(Node n : neighbors) {
			
			if(!sigOpen.get(n).get(signalId)) {
				continue;
			}
			
			final Edge e = graph.getEdge(source.getId(), n.getId());
			final double aggrCost = computeAggrCost(graph, e);
			final double time = simTime + aggrCost;
			schedule.add(new Event(signalId, time, e));
		}
		
		sigOpen.get(source).put(signalId, false);
	}

	private double computeAggrCost(Graph g, Edge e) {
		
		final List<Double> ms = new ArrayList<Double>(e.getMetrics());
		final int M = ms.size();
		final Node from = g.getNode(e.getFrom());
		
		double sum = 0.0;
		for(int m = 1; m < M; ++m) {
			double numerator = labels.get(from).get(m) + ms.get(m);
			double denominator = constraints.get(m - 1);
			sum += numerator / denominator;		
		}
		
		return sum;
	}

	private int numClosed(Node node) {
		int result = 0;
		for(Map.Entry<Integer, Boolean> entry : sigOpen.get(node).entrySet()) {
			if(!entry.getValue()) {
				++result;
			}
		}
		return result;
	}

	private Tree tryBuildingTree(Graph graph, Node candidate, List<Node> group) {
		
		List<EdgeDefinition> eds = new ArrayList<>();
		for(Node n : group) {
			
			// Helpers.
			Node target = n;
			Node current = candidate;
			Node next;
			
			if(current.equals(target)) {
				break;
			}
			
			do {
				next = predecessors.get(current).get(target);
				
				if(next.equals(current)) {
					return null;
				}
					
				eds.add(new EdgeDefinition(next.getId(), current.getId()));
				
				current = next;
				
			} while(!next.equals(target));
		}
		
		return new Tree(graph, eds);
	}

}
