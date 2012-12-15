package impossible.tfind.xamcra;

import impossible.model.topology.Node;

public class PathNode {
	private final Node node;
	private final int k;	
	private final PathNode prev;

	public PathNode(Node node, int k, PathNode prev) {
		this.node = node;
		this.k = k;
		this.prev = prev;
	}
	
	public Node getNode() {
		return node;
	}

	public int getK() {
		return k;
	}

	public PathNode getPrev() {
		return prev;
	}

	@Override
	public String toString() {
		return "{" + node + "[" + k + "] <- " + prev + "}";
	}
	
	// Note that the equality and the hash code functions are recursive.
	// Should it turn out inefficient, cutting the recursion may be
	// considered once it is checked to be guaranteed that comparing the
	// heads of the paths in case of the algorithm run is sufficient.
	// This will, however, not be sufficient in the general case, which
	// should be kept in mind!

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + k;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + ((prev == null) ? 0 : prev.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathNode other = (PathNode) obj;
		if (k != other.k)
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (prev == null) {
			if (other.prev != null)
				return false;
		} else if (!prev.equals(other.prev))
			return false;
		return true;
	}	
}