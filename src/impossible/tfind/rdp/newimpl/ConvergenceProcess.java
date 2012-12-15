package impossible.tfind.rdp.newimpl;

import impossible.model.topology.Node;

public interface ConvergenceProcess {
	double nextEventTime();
	Node handleNextEvent();
	boolean isDone();
}
