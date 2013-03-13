package edu.put.et.stik.mm.tfind;

import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Tree;

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
