package impossible.tfind.xamcra;

import impossible.model.topology.Node;

class PathNode {
	public final Node node;
	public final int k;	
	public final PathNode prev;
	public double label;

	public PathNode(Node node, int k, PathNode prev) {
		this.node = node;
		this.k = k;
		this.prev = prev;
		this.label = Double.NEGATIVE_INFINITY;
	}
	
	public void setLabel(double label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + k;
		long temp;
		temp = Double.doubleToLongBits(label);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		if (Double.doubleToLongBits(label) != Double
				.doubleToLongBits(other.label))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}
}