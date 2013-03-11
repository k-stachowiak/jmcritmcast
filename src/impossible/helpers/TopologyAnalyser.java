package impossible.helpers;

import impossible.model.topology.Graph;
import impossible.tfind.SpanningTreeFinder;

import java.util.List;


public interface TopologyAnalyser {

	boolean isConnected(Graph graph, SpanningTreeFinder spanningTreeFinder);
	List<Double> sumGraphMetrics(Graph graph);
	boolean equal(Graph a, Graph b);
	
}
