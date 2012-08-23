package impossible.helpers;

import impossible.model.SubGraph;

import java.util.List;

public interface ConstraintsComparer {
	boolean fulfilsAll(SubGraph subGraph, List<Double> constraints);

	boolean breaksAll(SubGraph subGraph, List<Double> constraints);

	boolean fulfilsIndex(SubGraph subGraph, int m, double constraint);
}