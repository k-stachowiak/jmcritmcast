package helpers;


import java.util.List;

import model.topology.Graph;

import tfind.SpanningTreeFinder;



public interface TopologyAnalyser {

	boolean isConnected(Graph graph, SpanningTreeFinder spanningTreeFinder);
	List<Double> sumGraphMetrics(Graph graph);
	boolean equal(Graph a, Graph b);
	
}
