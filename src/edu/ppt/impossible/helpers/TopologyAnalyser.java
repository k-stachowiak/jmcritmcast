package edu.ppt.impossible.helpers;

import java.util.List;

import edu.ppt.impossible.model.Graph;

public interface TopologyAnalyser {

	boolean isConnected(Graph graph);
	List<Double> sumGraphMetrics(Graph graph);
	
}
