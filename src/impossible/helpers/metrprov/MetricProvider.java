package impossible.helpers.metrprov;

import impossible.model.Edge;
import impossible.model.SubGraph;

public abstract class MetricProvider {

	public abstract Double get(Edge edge);

	public double getAdditive(SubGraph subGraph) {
		double result = 0;
		for (Edge edge : subGraph.getEdges())
			result += get(edge);
		return result;
	}

}
