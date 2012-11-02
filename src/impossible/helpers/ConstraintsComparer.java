package impossible.helpers;

import impossible.model.topology.SubGraph;

import java.util.List;

public interface ConstraintsComparer {

	public abstract boolean fulfilsAll(SubGraph subGraph,
			List<Double> constraints);

	public abstract boolean breaksAll(SubGraph subGraph,
			List<Double> constraints);

	public abstract boolean fulfilsIndex(SubGraph subGraph, int m,
			double constraint);

}