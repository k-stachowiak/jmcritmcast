package helpers.nodegrp;

import java.util.List;

import model.topology.Graph;
import model.topology.Node;

public class NodeGroupMetricDensity implements NodeGroupMetric {

	@Override
	public double get(List<Node> group, Graph graph) {
		return (double)group.size() / (double)graph.getNumNodes();
	}

}
