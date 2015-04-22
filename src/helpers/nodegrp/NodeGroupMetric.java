package helpers.nodegrp;

import java.util.List;

import model.topology.Graph;
import model.topology.Node;

public interface NodeGroupMetric {
	double get(List<Node> group, Graph graph);
}
