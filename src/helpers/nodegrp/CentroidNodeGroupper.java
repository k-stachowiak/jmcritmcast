package helpers.nodegrp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.topology.Graph;
import model.topology.Node;


public class CentroidNodeGroupper implements NodeGroupper {

	private final double cx;
	private final double cy;

	public CentroidNodeGroupper(double cx, double cy) {
		this.cx = cx;
		this.cy = cy;
	}

	@Override
	public List<Node> group(Graph graph, int groupSize) {
		List<Node> sorted = new ArrayList<>(graph.getNodes());
		Collections.sort(sorted, new CentroidDistanceNodeComparator(cx, cy));		
		return new ArrayList<Node>(sorted.subList(0, groupSize));
	}

}
