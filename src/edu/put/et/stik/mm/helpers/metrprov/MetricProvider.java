package edu.put.et.stik.mm.helpers.metrprov;

import java.util.ArrayList;
import java.util.List;

import edu.put.et.stik.mm.model.topology.Edge;
import edu.put.et.stik.mm.model.topology.SubGraph;


public abstract class MetricProvider {

	public abstract Double get(Edge edge);

	public double getPreAdditive(SubGraph subGraph) {
		double result = 0;
		for (Edge edge : subGraph.getEdges()) {
			result += get(edge);
                }
		return result;
	}
	
	public double getPostAdditive(SubGraph subGraph) {
		
		final int invalidId = -1;
		
		List<Double> metrics = new ArrayList<>();
		for(int i = 0; i < subGraph.getParent().getNumMetrics(); ++i) {
			metrics.add(0.0);
		}
		
		for(Edge edge : subGraph.getEdges()) {
			for(int i = 0; i < edge.getMetrics().size(); ++i) {
				metrics.set(i, metrics.get(i) + edge.getMetrics().get(i));
			}
		}
		
		Edge dummyEdge = new Edge(invalidId, invalidId, metrics);
		return get(dummyEdge);
	}

}
