package model.util;


import java.util.Comparator;

import model.topology.Node;


public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node lhs, Node rhs) {
		return Integer.compare(lhs.getId(), rhs.getId());
	}

}
