package impossible.model;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphFactory {

	public Graph createTest() {

		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0, 0));
		nodes.add(new Node(1, 2, 2));
		nodes.add(new Node(2, 4, 0));
		nodes.add(new Node(3, 1, 1));
		nodes.add(new Node(4, 3, 1));
		nodes.add(new Node(5, 2, 0));

		List<Double> metrics = new ArrayList<>();
		metrics.add(100.0);
		metrics.add(100.0);
		metrics.add(100.0);

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(3, 1, new ArrayList<Double>(metrics)));
		edges.add(new Edge(1, 4, new ArrayList<Double>(metrics)));
		edges.add(new Edge(4, 2, new ArrayList<Double>(metrics)));
		edges.add(new Edge(2, 5, new ArrayList<Double>(metrics)));
		edges.add(new Edge(5, 0, new ArrayList<Double>(metrics)));
		edges.add(new Edge(3, 4, new ArrayList<Double>(metrics)));
		edges.add(new Edge(4, 5, new ArrayList<Double>(metrics)));
		edges.add(new Edge(5, 3, new ArrayList<Double>(metrics)));

		return createFromLists(nodes, edges);
	}

	public Graph createDisconnected() {

		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0, 0));
		nodes.add(new Node(1, 1, 1));
		nodes.add(new Node(2, 2, 2));
		nodes.add(new Node(3, 3, 3));
		nodes.add(new Node(4, 4, 4));

		List<Double> metrics = new ArrayList<>();
		metrics.add(100.0);
		metrics.add(100.0);
		metrics.add(100.0);

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 2, new ArrayList<Double>(metrics)));
		edges.add(new Edge(1, 2, new ArrayList<Double>(metrics)));
		edges.add(new Edge(3, 4, new ArrayList<Double>(metrics)));

		return createFromLists(nodes, edges);

	}

	public Graph createNontruncatedNodeIds() {
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(3, 0, 0));
		nodes.add(new Node(7, 1, 1));

		List<Double> metrics = new ArrayList<>();
		metrics.add(100.0);
		metrics.add(100.0);
		metrics.add(100.0);

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(3, 7, new ArrayList<Double>(metrics)));

		return createFromLists(nodes, edges);
	}

	public Graph createDoubleTriangle(List<SubGraph.EdgeDefinition> cheapEdges) {

		final double expensive = 100;
		final double cheap = 5;

		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0.0, 0.0));
		nodes.add(new Node(1, 2.0, 2.0));
		nodes.add(new Node(2, 4.0, 0.0));
		nodes.add(new Node(3, 1.0, 1.0));
		nodes.add(new Node(4, 3.0, 1.0));
		nodes.add(new Node(5, 2.0, 0.0));

		List<SubGraph.EdgeDefinition> edgeDefinitions = new ArrayList<>();
		edgeDefinitions.add(new SubGraph.EdgeDefinition(0, 3));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(3, 1));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(1, 4));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(4, 2));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(2, 5));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(5, 0));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(3, 4));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(4, 5));
		edgeDefinitions.add(new SubGraph.EdgeDefinition(5, 3));

		List<Double> expensiveMetrics = new ArrayList<>();
		expensiveMetrics.add(expensive);
		expensiveMetrics.add(expensive);
		expensiveMetrics.add(expensive);

		List<Double> cheapMetrics = new ArrayList<>();
		cheapMetrics.add(cheap);
		cheapMetrics.add(cheap);
		cheapMetrics.add(cheap);

		List<Edge> edges = new ArrayList<>();
		for (SubGraph.EdgeDefinition edgeDefinition : edgeDefinitions) {

			boolean isCheapStrainght = cheapEdges.contains(edgeDefinition);
			
			boolean isCheapReverse = cheapEdges
					.contains(new SubGraph.EdgeDefinition(edgeDefinition
							.getTo(), edgeDefinition.getFrom()));
			
			boolean isCheap = isCheapStrainght | isCheapReverse;

			List<Double> metrics = isCheap ? cheapMetrics : expensiveMetrics;

			edges.add(new Edge(edgeDefinition.getFrom(),
					edgeDefinition.getTo(), metrics));
		}

		return createFromLists(nodes, edges);
	}

	public abstract Graph createFromLists(List<Node> nodes, List<Edge> edges);

}
