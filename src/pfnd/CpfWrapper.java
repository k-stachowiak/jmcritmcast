package pfnd;

import java.util.List;

import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;


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
