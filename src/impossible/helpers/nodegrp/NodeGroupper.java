package impossible.helpers.nodegrp;

import impossible.model.Graph;
import impossible.model.Node;

import java.util.List;


public interface NodeGroupper {

	List<Node> group(Graph graph, int groupSize);

}
