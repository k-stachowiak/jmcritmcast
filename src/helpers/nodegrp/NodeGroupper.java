package helpers.nodegrp;


import java.util.List;

import model.topology.Graph;
import model.topology.Node;



public interface NodeGroupper {

	List<Node> group(Graph graph, int groupSize);

}
