package impossible.tfind.rdp.newimpl;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;

import java.util.HashMap;
import java.util.Map;

public class SimpleVariant implements Variant {

	private Map<Node, Map<Node, Node>> predecessorMaps;
	
	public SimpleVariant() {
		predecessorMaps = new HashMap<>();
	}

	@Override
	public ConvergenceProcess initConvergenceProcess(Graph graph, Node source) {

		// Initialize the guts and the result..
		Map<Node, Double> costMap = new HashMap<>();
		Map<Node, Node> predecessorMap = new HashMap<>();
		ConvergenceProcess convergenceProcess = new CostConvergenceProcess(
				graph, costMap, predecessorMap, source);
		
		// Register the elements locally.
		predecessorMaps.put(source, predecessorMap);
		
		// Return the result.
		return convergenceProcess;
	}

	@Override
	public ResultBuildProcess initResultBuildProcess(Graph graph, Node rdp) {
		return new SimpleResultBuildProcess(graph, predecessorMaps, rdp);
	}

}
