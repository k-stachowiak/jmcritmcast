package tfind.prim;


import helpers.metrprov.MetricProvider;

import java.util.ArrayList;
import java.util.List;

import model.topology.Edge;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.Node;
import model.topology.Tree;

import tfind.SpanningTreeFinder;



public class PrimTreeFinder implements SpanningTreeFinder {

	public class Cut {
		private final Graph graph;
		private final MetricProvider metricProvider;

		private final List<Edge> cutEdges;
		private final List<Node> nodesInside;
		private final List<Edge> edgesInside;

		public Cut(Node root, Graph graph, MetricProvider metricProvider) {

			this.graph = graph;
			this.metricProvider = metricProvider;

			cutEdges = new ArrayList<>();
			nodesInside = new ArrayList<>();
			edgesInside = new ArrayList<>();

			nodesInside.add(root);
			addValidEdges(root);
		}

		public void expand() {

			// Find cheapest edge on the cut.
			Edge cheapest = findCheapestOnCut();

			// Move inside from cut
			cutEdges.remove(cheapest);
			edgesInside.add(cheapest);

			// Add the node inside
			Node from = graph.getNode(cheapest.getFrom());
			Node to = graph.getNode(cheapest.getTo());
			Node node = (nodesInside.contains(from)) ? to : from;
			nodesInside.add(node);

			// Add new edges to the cut
			addValidEdges(node);

			// Remove acquired edges
			removeInvalidEdges();
		}

		public boolean canExpand() {			
			return cutEdges.size() > 0;
		}

		public boolean graphContained() {
			return nodesInside.size() == graph.getNumNodes();
		}

		public Tree buildTree() {
			List<EdgeDefinition> edgeDefinitions = new ArrayList<>();
			for (Edge edge : edgesInside) {
				edgeDefinitions.add(new EdgeDefinition(edge.getFrom(),
						edge.getTo()));
			}
			return new Tree(graph, edgeDefinitions);
		}

		private Edge findCheapestOnCut() {
			Edge candidate = null;
			double cheapest = Double.POSITIVE_INFINITY;
			for (Edge edge : cutEdges) {
				double cost = metricProvider.get(edge);
				if (candidate == null || cost < cheapest) {
					candidate = edge;
					cheapest = cost;
				}
			}
			return candidate;
		}

		private void addValidEdges(Node node) {

			// Acquire neighbors
			List<Node> neighbors = graph.getNeighbors(node);

			for (Node neighbor : neighbors) {
				Edge edge = graph.getEdge(node.getId(), neighbor.getId());
				if (cutEdges.contains(edge))
					continue;

				if (edgesInside.contains(edge))
					continue;

				cutEdges.add(edge);
			}
		}

		private void removeInvalidEdges() {

			List<Edge> toBeRemoved = new ArrayList<>();

			for (Edge edge : cutEdges) {
				Node from = graph.getNode(edge.getFrom());
				Node to = graph.getNode(edge.getTo());
				if (nodesInside.contains(from) && nodesInside.contains(to))
					toBeRemoved.add(edge);
			}

			for (Edge edge : toBeRemoved)
				cutEdges.remove(edge);
		}
	}

	private final MetricProvider metricProvider;

	public PrimTreeFinder(MetricProvider metricProvider) {
		super();
		this.metricProvider = metricProvider;
	}

	@Override
	public Tree find(Node root, Graph graph) {

		Cut cut = new Cut(root, graph, metricProvider);
		while (!cut.graphContained() && cut.canExpand())
			cut.expand();
		return cut.buildTree();
	}

}
