package tfind;

import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Tree;


public class CtfWrapper implements SteinerTreeFinder {
	
	private final ConstrainedSteinerTreeFinder inner;
	private final List<Double> constraints;

	public CtfWrapper(ConstrainedSteinerTreeFinder inner,
			List<Double> constraints) {
		super();
		this.inner = inner;
		this.constraints = constraints;
	}

	@Override
	public Tree find(Graph graph, List<Node> group) {
		return inner.find(graph, group, constraints);
	}

}
