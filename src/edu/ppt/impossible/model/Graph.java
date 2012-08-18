package edu.ppt.impossible.model;

import java.util.List;

public interface Graph {
	
	Graph copy();

	int getNumNodes();
	int getNumMetrics();
	
	List<Node> getNodes();
	List<Edge> getEdges();

	Node getNode(int node);
	Edge getEdge(int from, int to);

	List<Node> getNeighbors(Node from);
	
	public int hashCode();
	public boolean equals(Object obj);
}
