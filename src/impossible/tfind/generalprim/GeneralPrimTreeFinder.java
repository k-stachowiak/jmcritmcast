package impossible.tfind.generalprim;

import impossible.model.topology.Edge;
import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.SpanningTreeFinder;

import java.util.ArrayList;
import java.util.List;

public class GeneralPrimTreeFinder implements SpanningTreeFinder {

	public class Cut {
		private final Graph graph;
		private final EdgeSelector edgeSelector;

		private final List<Edge> cutEdges;
		private final List<Node> nodesInside;
		private final List<Edge> edgesInside;

		public Cut(Node root, Graph graph, EdgeSelector edgeSelector) {

			this.graph = graph;
			this.edgeSelector = edgeSelector;

			cutEdges = new ArrayList<>();
			nodesInside = new ArrayList<>();
			edgesInside = new ArrayList<>();

			nodesInside.add(root);
			addValidEdges(root);
		}

		public void expand() {

			// Find cheapest edge on the cut.
			Edge cheapest = edgeSelector.select(cutEdges);

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
				edgeDefinitions.add(new EdgeDefinition(edge.getFrom(), edge
						.getTo()));
			}
			return new Tree(graph, edgeDefinitions);
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

	private final EdgeSelector edgeSelector;

	public GeneralPrimTreeFinder(EdgeSelector edgeSelector) {
		this.edgeSelector = edgeSelector;
	}

	@Override
	public Tree find(Node root, Graph graph) {
		edgeSelector.reset();
		Cut cut = new Cut(root, graph, edgeSelector);
		while (!cut.graphContained() && cut.canExpand())
			cut.expand();
		return cut.buildTree();
	}

}
