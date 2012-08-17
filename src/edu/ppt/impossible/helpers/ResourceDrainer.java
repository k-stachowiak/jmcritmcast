package edu.ppt.impossible.helpers;

import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.SubGraph;

public interface ResourceDrainer {

	Graph drain(Graph graph, SubGraph subGraph);

}
