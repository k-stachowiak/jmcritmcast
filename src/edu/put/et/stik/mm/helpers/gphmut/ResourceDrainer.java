package edu.put.et.stik.mm.helpers.gphmut;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.SubGraph;

public interface ResourceDrainer {

	Graph drain(Graph graph, SubGraph subGraph, double resources, double minResource);

}
