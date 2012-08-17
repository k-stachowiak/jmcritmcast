package edu.ppt.impossible.model;

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
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));
		edges.add(new Edge(0, 3, new ArrayList<Double>(metrics)));

		return createFromLists(nodes, edges);
	}

	public abstract Graph createFromLists(List<Node> nodes, List<Edge> edges);

}
