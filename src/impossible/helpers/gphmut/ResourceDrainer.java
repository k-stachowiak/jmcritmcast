package impossible.helpers.gphmut;

import impossible.model.topology.Graph;
import impossible.model.topology.SubGraph;

public interface ResourceDrainer {

	Graph drain(Graph graph, SubGraph subGraph, double resources, double minResource);

}
