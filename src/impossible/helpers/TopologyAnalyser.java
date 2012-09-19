package impossible.helpers;

import impossible.model.topology.Graph;

import java.util.List;


public interface TopologyAnalyser {

	boolean isConnected(Graph graph);
	List<Double> sumGraphMetrics(Graph graph);
	
}
