package helpers.nodegrp;

import java.util.Comparator;

import model.topology.Graph;
import model.topology.Node;

public class DegreeNodeComparator implements Comparator<Node> {
	
	private final Graph graph;

	public DegreeNodeComparator(Graph graph) {
		this.graph = graph;
	}

	@Override
	public int compare(Node lhs, Node rhs) {
		int lhsDegree = graph.getNeighbors(lhs).size();
		int rhsDegree = graph.getNeighbors(rhs).size();
		return Integer.compare(lhsDegree, rhsDegree); 
	}

}
