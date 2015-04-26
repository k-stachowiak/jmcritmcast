package model.topology;


import java.util.Comparator;


public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node lhs, Node rhs) {
		return Integer.compare(lhs.getId(), rhs.getId());
	}

}
