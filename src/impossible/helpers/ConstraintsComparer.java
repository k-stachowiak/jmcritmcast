package impossible.helpers;

import impossible.model.SubGraph;

import java.util.List;

public interface ConstraintsComparer {
	boolean fulfilsConstraints(SubGraph subGraph, List<Double> constraints);
}