package edu.put.et.stik.mm.model.util;


import java.util.Comparator;

import edu.put.et.stik.mm.model.topology.Node;

public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node lhs, Node rhs) {
		return Integer.compare(lhs.getId(), rhs.getId());
	}

}
