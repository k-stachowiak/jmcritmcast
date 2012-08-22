package impossible.helpers;

import impossible.model.Graph;

import java.util.List;


public interface TopologyAnalyser {

	boolean isConnected(Graph graph);
	List<Double> sumGraphMetrics(Graph graph);
	
}
