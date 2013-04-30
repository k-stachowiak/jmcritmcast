package model.topology;

import java.util.List;


public interface Graph {
	
	Graph copy();

	int getNumNodes();
	int getNumEdges();
	int getNumMetrics();
	
	List<Node> getNodes();
	List<Edge> getEdges();

	Node getNode(int node);
	Edge getEdge(int from, int to);

	List<Node> getNeighbors(Node from);
	List<Node> getPredecessors(Node to);
	
	public int hashCode();
	public boolean equals(Object obj);
}
