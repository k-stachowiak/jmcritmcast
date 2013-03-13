package edu.put.et.stik.mm.pfnd;

import java.util.List;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;

public class CpfWrapper implements PathFinder {
	
	private final ConstrainedPathFinder inner;
	private final List<Double> constraints;

	public CpfWrapper(ConstrainedPathFinder inner, List<Double> constraints) {
		this.inner = inner;
		this.constraints = constraints;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {
		return inner.find(graph, from, to, constraints);
	}

}
