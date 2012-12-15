package impossible.tfind.rdp.newimpl;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;

public interface Variant {
	
	ConvergenceProcess initConvergenceProcess(Graph graph, Node source);

	ResultBuildProcess initResultBuildProcess(Graph graph, Node rdp);
	
}
