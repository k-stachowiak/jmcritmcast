package impossible.tfind.generalprim;

import java.util.List;

import impossible.model.topology.Edge;

// Note that objects of this type may be dangerously stateful!
public interface EdgeSelector {
	
	void reset();

	Edge select(List<Edge> cutEdges);
	
}
