package model.topology;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;


public abstract class GraphFactory {

	// Regular topologies.
	// -------------------

	public Graph createDoubleQuad(List<EdgeDefinition> cheapEdges) {

		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 1.0, 2.0));
		nodes.add(new Node(1, 0.0, 1.0));
		nodes.add(new Node(2, 0.0, 3.0));
		nodes.add(new Node(3, 1.0, 4.0));
		nodes.add(new Node(4, 2.0, 3.0));
		nodes.add(new Node(5, 2.0, 1.0));
		nodes.add(new Node(6, 1.0, 0.0));

		List<EdgeDefinition> edgeDefinitions = new ArrayList<>();

		// The edges inside.
		edgeDefinitions.add(new EdgeDefinition(0, 2));
		edgeDefinitions.add(new EdgeDefinition(0, 4));
		edgeDefinitions.add(new EdgeDefinition(0, 6));

		// The edges around.
		edgeDefinitions.add(new EdgeDefinition(1, 2));
		edgeDefinitions.add(new EdgeDefinition(2, 3));
		edgeDefinitions.add(new EdgeDefinition(3, 4));
		edgeDefinitions.add(new EdgeDefinition(4, 5));
		edgeDefinitions.add(new EdgeDefinition(5, 6));
		edgeDefinitions.add(new EdgeDefinition(6, 1));

		final double expensiveMetric = 100;
		final double cheapMetric = 5;


		return createPartiallyCheap(nodes, edgeDefinitions, cheapEdges,
				cheapMetric, expensiveMetric, 3);
	}

	public Graph createDoubleTriangle(List<EdgeDefinition> cheapEdges) {

		final List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0.0, 0.0));
		nodes.add(new Node(1, 2.0, 2.0));
		nodes.add(new Node(2, 4.0, 0.0));
		nodes.add(new Node(3, 1.0, 1.0));
		nodes.add(new Node(4, 3.0, 1.0));
		nodes.add(new Node(5, 2.0, 0.0));

		final List<EdgeDefinition> edgeDefinitions = new ArrayList<>();
		edgeDefinitions.add(new EdgeDefinition(0, 3));
		edgeDefinitions.add(new EdgeDefinition(3, 1));
		edgeDefinitions.add(new EdgeDefinition(1, 4));
		edgeDefinitions.add(new EdgeDefinition(4, 2));
		edgeDefinitions.add(new EdgeDefinition(2, 5));
		edgeDefinitions.add(new EdgeDefinition(5, 0));
		edgeDefinitions.add(new EdgeDefinition(3, 4));
		edgeDefinitions.add(new EdgeDefinition(4, 5));
		edgeDefinitions.add(new EdgeDefinition(5, 3));

		final double expensiveMetric = 100;
		final double cheapMetric = 5;

		return createPartiallyCheap(nodes, edgeDefinitions, cheapEdges,
				cheapMetric, expensiveMetric, 3);
	}
	
	public Graph createOneEdge(List<Double> metrics) {
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0.0, 0.0));
		nodes.add(new Node(1, 1.0, 0.0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, new ArrayList<>(metrics)));

		return createFromLists(nodes, edges);
	}
	
	public Graph createPathGraph(List<List<Double>> metrics) {

		final double anyDouble = -1.0;
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < (metrics.size() + 1); ++i) {
			nodes.add(new Node(i, anyDouble, anyDouble));
		}

		List<Edge> edges = new ArrayList<>();
		for (int i = 0; i < metrics.size(); ++i) {
			edges.add(new Edge(i, i + 1, new ArrayList<>(metrics.get(i))));
		}

		return createFromLists(nodes, edges);
	}

	public Graph createNSimplePaths1m(List<Double> metric) {

		double anyDouble = 1.0;
		int id = 0;

		int numNodes = metric.size() + 2;
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < numNodes; ++i) {
			nodes.add(new Node(id++, anyDouble, anyDouble));
		}

		int first = 0;
		int last = numNodes - 1;
		List<Edge> edges = new ArrayList<>();
		for (int i = 0; i < metric.size(); ++i) {
			List<Double> halfMetrics = new ArrayList<>();
			double half = metric.get(i) * 0.5;
			halfMetrics.add(half);
			int currentMiddle = i + 1;
			edges.add(new Edge(first, currentMiddle, halfMetrics));
			edges.add(new Edge(currentMiddle, last, halfMetrics));
		}

		return createFromLists(nodes, edges);
	}

	// metrics indexing: metrics[path][metric]
	public Graph createNSimplePathNm(List<List<Double>> metrics) {

		double anyDouble = 1.0;
		int id = 0;

		int numNodes = metrics.size() + 2;
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < numNodes; ++i) {
			nodes.add(new Node(id++, anyDouble, anyDouble));
		}

		int first = 0;
		int last = numNodes - 1;
		List<Edge> edges = new ArrayList<>();
		for (int i = 0; i < metrics.size(); ++i) {
			List<Double> halfMetrics = new ArrayList<>();
			for (int j = 0; j < metrics.get(i).size(); ++j) {
				double half = metrics.get(i).get(j) * 0.5;
				halfMetrics.add(half);
			}
			int currentMiddle = i + 1;
			edges.add(new Edge(first, currentMiddle, halfMetrics));
			edges.add(new Edge(currentMiddle, last, halfMetrics));
		}

		return createFromLists(nodes, edges);
	}

	public Graph createInfLoop() {
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 1.0, 2.0));
		nodes.add(new Node(1, 2.0, 3.0));
		nodes.add(new Node(2, 3.0, 4.0));
		nodes.add(new Node(3, 4.0, 5.0));
		nodes.add(new Node(4, 5.0, 6.0));
		nodes.add(new Node(5, 6.0, 7.0));
		nodes.add(new Node(6, 7.0, 8.0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 2.0, 1.0 })));
		edges.add(new Edge(0, 2, Arrays.asList(new Double[] { 1.0, 3.0 })));
		edges.add(new Edge(1, 3, Arrays.asList(new Double[] { 3.0, 2.0 })));
		edges.add(new Edge(2, 3, Arrays.asList(new Double[] { 2.0, 2.0 })));
		edges.add(new Edge(3, 4, Arrays.asList(new Double[] { 3.0, 1.0 })));
		edges.add(new Edge(3, 5, Arrays.asList(new Double[] { 3.0, 3.0 })));
		edges.add(new Edge(4, 6, Arrays.asList(new Double[] { 1.0, 4.0 })));
		edges.add(new Edge(5, 6, Arrays.asList(new Double[] { 2.0, 3.0 })));

		return createFromLists(nodes, edges);
	}
	
	// Specific topologies.
	// --------------------

	public Graph createBig2Metr() {
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 1.0, 2.0));
		nodes.add(new Node(1, 3.0, 4.0));
		nodes.add(new Node(2, 5.0, 6.0));
		nodes.add(new Node(3, 51.0, 2.0));
		nodes.add(new Node(4, 3.0, 4.0));
		nodes.add(new Node(5, 5.0, 6.0));
		nodes.add(new Node(6, 3.0, 4.0));
		nodes.add(new Node(7, 5.0, 6.0));
		nodes.add(new Node(8, 51.0, 2.0));
		nodes.add(new Node(9, 3.0, 4.0));
		nodes.add(new Node(10, 5.0, 6.0));
		nodes.add(new Node(11, 3.0, 4.0));
		nodes.add(new Node(12, 5.0, 6.0));
		nodes.add(new Node(13, 5.0, 6.0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 1.0, 1.0, 20.0 })));
		edges.add(new Edge(1, 2, Arrays
				.asList(new Double[] { 3.0, 130.0, 4.0 })));
		edges.add(new Edge(1, 4, Arrays.asList(new Double[] { 4.0, 5.0, 12.0 })));
		edges.add(new Edge(2, 5, Arrays.asList(new Double[] { 6.0, 3.0, 14.0 })));
		edges.add(new Edge(2, 3, Arrays
				.asList(new Double[] { 7.0, 5.0, 130.0 })));
		edges.add(new Edge(3, 4, Arrays
				.asList(new Double[] { 3.0, 130.0, 2.0 })));
		edges.add(new Edge(3, 11, Arrays.asList(new Double[] { 6.0, 5.0, 1.0 })));
		edges.add(new Edge(3, 5, Arrays.asList(new Double[] { 7.0, 10.0, 3.0 })));
		edges.add(new Edge(4, 7, Arrays.asList(new Double[] { 7.0, 3.0, 25.0 })));
		edges.add(new Edge(4, 6, Arrays.asList(new Double[] { 4.0, 3.0, 4.0 })));
		edges.add(new Edge(6, 8, Arrays.asList(new Double[] { 9.0, 3.0, 1.0 })));
		edges.add(new Edge(8, 9, Arrays.asList(new Double[] { 3.0, 5.0, 32.0 })));
		edges.add(new Edge(9, 10, Arrays.asList(new Double[] { 3.0, 5.0, 8.0 })));
		edges.add(new Edge(10, 11, Arrays
				.asList(new Double[] { 7.0, 3.0, 12.0 })));
		edges.add(new Edge(11, 12, Arrays
				.asList(new Double[] { 5.0, 5.0, 13.0 })));
		edges.add(new Edge(12, 13, Arrays
				.asList(new Double[] { 6.0, 3.0, 13.0 })));

		return createFromLists(nodes, edges);
	}

	public Graph createMaciejPiechowiakExample() {

		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0.0, 0.0));
		nodes.add(new Node(1, 0.0, 0.0));
		nodes.add(new Node(2, 0.0, 0.0));
		nodes.add(new Node(3, 0.0, 0.0));
		nodes.add(new Node(4, 0.0, 0.0));
		nodes.add(new Node(5, 0.0, 0.0));
		nodes.add(new Node(6, 0.0, 0.0));
		nodes.add(new Node(7, 0.0, 0.0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 4, Arrays.asList(new Double[] { 394.0, 332.0 })));
		edges.add(new Edge(0, 6, Arrays.asList(new Double[] { 196.0, 346.0 })));
		edges.add(new Edge(1, 7, Arrays.asList(new Double[] { 461.0, 991.0 })));
		edges.add(new Edge(1, 6, Arrays.asList(new Double[] { 445.0, 598.0 })));
		edges.add(new Edge(2, 1, Arrays.asList(new Double[] { 354.0, 469.0 })));
		edges.add(new Edge(2, 0, Arrays.asList(new Double[] { 313.0, 159.0 })));
		edges.add(new Edge(3, 2, Arrays.asList(new Double[] { 417.0, 697.0 })));
		edges.add(new Edge(3, 1, Arrays.asList(new Double[] { 170.0, 1082.0 })));
		edges.add(new Edge(4, 2, Arrays.asList(new Double[] { 787.0, 254.0 })));
		edges.add(new Edge(4, 3, Arrays.asList(new Double[] { 107.0, 443.0 })));
		edges.add(new Edge(5, 0, Arrays.asList(new Double[] { 33.0, 1014.0 })));
		edges.add(new Edge(5, 1, Arrays.asList(new Double[] { 299.0, 771.0 })));
		edges.add(new Edge(6, 4, Arrays.asList(new Double[] { 108.0, 93.0 })));
		edges.add(new Edge(6, 2, Arrays.asList(new Double[] { 347.0, 227.0 })));
		edges.add(new Edge(7, 5, Arrays.asList(new Double[] { 402.0, 570.0 })));
		edges.add(new Edge(7, 4, Arrays.asList(new Double[] { 199.0, 561.0 })));

		return createFromLists(nodes, edges);
	}
	
	// Specific costs.
	// ---------------
	
	public Graph createPartiallyCheap(List<Node> nodes,
			List<EdgeDefinition> edgeDefinitions,
			List<EdgeDefinition> cheapEdges, double cheapMetric,
			double expensiveMetric, int numMetrics) {

		// Prepare the metrics lists.
		List<Double> expensiveMetrics = new ArrayList<>();
		List<Double> cheapMetrics = new ArrayList<>();
		for (int i = 0; i < numMetrics; ++i) {
			expensiveMetrics.add(expensiveMetric);
			cheapMetrics.add(cheapMetric);
		}

		// Generate edges from the definitions.
		List<Edge> edges = new ArrayList<>();
		for (EdgeDefinition edgeDefinition : edgeDefinitions) {

			// Determine if the given edge is to be cheap or not.
			boolean isCheapStrainght = cheapEdges.contains(edgeDefinition);
			boolean isCheapReverse = cheapEdges.contains(new EdgeDefinition(
					edgeDefinition.getTo(), edgeDefinition.getFrom()));
			boolean isCheap = isCheapStrainght | isCheapReverse;

			// Add a proper edge to the result.
			List<Double> metrics = isCheap ? cheapMetrics : expensiveMetrics;
			edges.add(new Edge(edgeDefinition.getFrom(),
					edgeDefinition.getTo(), metrics));
		}

		return createFromLists(nodes, edges);
	}

	
	
	// Peculiar cases.
	// ---------------
	
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

	public Graph createFromDTO(GraphDTO graphDTO) {

		List<Node> nodes = new ArrayList<>();
		for (NodeDTO nodeDTO : graphDTO.getNodes()) {
			nodes.add(new Node(nodeDTO.getId(), nodeDTO.getX(), nodeDTO.getY()));
		}

		List<Edge> edges = new ArrayList<>();
		for (EdgeDTO edgeDTO : graphDTO.getEdges()) {
			edges.add(new Edge(edgeDTO.getNodeFrom(), edgeDTO.getNodeTo(),
					new ArrayList<>(edgeDTO.getMetrics())));
		}

		return createFromLists(nodes, edges);
	}

	

	public static GraphDTO createDTO(Graph graph) {

		List<NodeDTO> nodes = new ArrayList<>();
		for (Node node : graph.getNodes()) {
			nodes.add(new NodeDTO(node.getId(), node.getX(), node.getY()));
		}

		List<EdgeDTO> edges = new ArrayList<>();
		for (Edge edge : graph.getEdges()) {
			edges.add(new EdgeDTO(edge.getFrom(), edge.getTo(),
					new ArrayList<>(edge.getMetrics())));
		}

		return new GraphDTO(nodes, edges);
	}
	
	// Subgraph generation.
	// --------------------

	public Path createPath(int numEdges, List<Double> edgeMetrics) {

		final double anyDouble = -1.0;

		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < numEdges + 1; ++i) {
			nodes.add(new Node(i, anyDouble, anyDouble));
		}

		List<Edge> edges = new ArrayList<>();
		for (int i = 0; i < numEdges; ++i) {
			edges.add(new Edge(i, i + 1, new ArrayList<>(edgeMetrics)));
		}

		Graph parent = createFromLists(nodes, edges);

		List<Integer> nodeIds = new ArrayList<>();
		for (int i = 0; i < numEdges + 1; ++i) {
			nodeIds.add(i);
		}

		return new Path(parent, nodeIds);
	}

	// TODO: Make this method accept a boolean argument to determine if
	// the graph should be directed or not.
	public abstract Graph createFromLists(List<Node> nodes, List<Edge> edges);
}
