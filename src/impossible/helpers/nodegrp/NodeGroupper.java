package impossible.helpers.nodegrp;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;

import java.util.List;


public interface NodeGroupper {

	List<Node> group(Graph graph, int groupSize);

}
