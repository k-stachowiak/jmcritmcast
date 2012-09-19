package impossible.helpers.metrprov;

import impossible.model.topology.Edge;
import impossible.model.topology.SubGraph;

public abstract class MetricProvider {

	public abstract Double get(Edge edge);

	public double getPreAdditive(SubGraph subGraph) {
		double result = 0;
		for (Edge edge : subGraph.getEdges()) {
			result += get(edge);
                }
		return result;
	}

}
