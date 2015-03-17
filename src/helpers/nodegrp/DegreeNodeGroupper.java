package helpers.nodegrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.topology.Graph;
import model.topology.Node;

public class DegreeNodeGroupper implements NodeGroupper {

	@Override
	public List<Node> group(Graph graph, int groupSize) {
		List<Node> sorted = new ArrayList<>(graph.getNodes());
		Collections.sort(sorted, new DegreeNodeComparator(graph));
		return new ArrayList<Node>(sorted.subList(sorted.size() - groupSize, sorted.size()));
	}

}
