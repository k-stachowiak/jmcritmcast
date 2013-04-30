package helpers.nodegrp;


import java.util.Comparator;

import model.topology.Node;


public class CentroidDistanceNodeComparator implements Comparator<Node> {

	private final double cx;
	private final double cy;

	public CentroidDistanceNodeComparator(double cx, double cy) {
		this.cx = cx;
		this.cy = cy;
	}

	@Override
	public int compare(Node lhs, Node rhs) {

		double lhsDx = lhs.getX() - cx;
		double lhsDy = lhs.getY() - cy;
		double lhsDistSqr = lhsDx * lhsDx + lhsDy * lhsDy;
		
		double rhsDx = rhs.getX() - cx;
		double rhsDy = rhs.getY() - cy;
		double rhsDistSqr = rhsDx * rhsDx + rhsDy * rhsDy;

		// Note: Since the "square root" function is monotonous there is no need
		// to perform it if the results are only to be compared.
		return Double.compare(lhsDistSqr, rhsDistSqr);
	}

}
