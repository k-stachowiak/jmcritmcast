package helpers.gphmut;

import model.topology.Graph;
import model.topology.SubGraph;

public interface ResourceDrainer {

	Graph drain(Graph graph, SubGraph subGraph, double resources, double minResource);

}
