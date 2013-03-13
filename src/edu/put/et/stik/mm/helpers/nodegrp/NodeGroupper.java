package edu.put.et.stik.mm.helpers.nodegrp;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;


public interface NodeGroupper {

	List<Node> group(Graph graph, int groupSize);

}
