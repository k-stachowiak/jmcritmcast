package edu.put.et.stik.mm.helpers;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.tfind.SpanningTreeFinder;


public interface TopologyAnalyser {

	boolean isConnected(Graph graph, SpanningTreeFinder spanningTreeFinder);
	List<Double> sumGraphMetrics(Graph graph);
	boolean equal(Graph a, Graph b);
	
}
