package helpers.nodegrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import helpers.TopologyAnalyser;
import model.topology.Graph;
import model.topology.Node;

public class CentroidNodeGroupper implements NodeGroupper {

	private static Random random = new Random(System.nanoTime());
	private final double coefficient;

	public CentroidNodeGroupper(double coefficient) {
		this.coefficient = coefficient;
	}

	@Override
	public List<Node> group(Graph graph, int groupSize) {

		TopologyAnalyser.MinMaxSquare minMaxSquare = TopologyAnalyser.minMaxCoordinates(graph);

		double rx = (minMaxSquare.getMaxX() - minMaxSquare.getMinX()) / 2.0;
		double ry = (minMaxSquare.getMaxY() - minMaxSquare.getMinY()) / 2.0;
		double r = (rx + ry) / 2.0;
		double selectionRadius = r * coefficient;

		List<Node> nodes = new ArrayList<>(graph.getNodes());
		int removedIndex = random.nextInt(nodes.size());
		Node firstNode = nodes.get(removedIndex);
		nodes.remove(removedIndex);

		List<Node> inRadius = new ArrayList<>();
		for (Node n : nodes) {
			double dx = n.getX() - firstNode.getX();
			double dy = n.getY() - firstNode.getY();
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance <= selectionRadius) {
				inRadius.add(n);
			}
		}

		List<Node> result;
		if (inRadius.size() >= groupSize - 1) {
			Collections.shuffle(inRadius);
			result = inRadius.subList(0, groupSize - 1);
			
		} else {
			Collections.sort(nodes, new CentroidDistanceNodeComparator(firstNode.getX(), firstNode.getY()));
			result = nodes.subList(0, groupSize - 1);
		}
		result.add(firstNode);
		return result;
	}

}
