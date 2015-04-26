package aplfnd;

import helpers.metrprov.MetricProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.IllegalOperationException;
import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodePair;

public class FloydWarshallAllPathLengthFinder implements AllPathLengthFinder {

	@Override
	public Map<NodePair, Double> find(Graph graph, MetricProvider metricProvider) {

		List<Node> nodes = graph.getNodes();

		HashMap<NodePair, Double> result = new HashMap<>();

		for (Node u : nodes) {
			for (Node v : nodes) {
				if (u.equals(v)) {
					result.put(new NodePair(u, v), 0.0);
				} else {
					try {
						Edge e = graph.getEdge(u.getId(), v.getId());
						result.put(new NodePair(u, v), metricProvider.get(e));
					} catch (IllegalOperationException ex) {
						result.put(new NodePair(u, v), Double.POSITIVE_INFINITY);
					}
				}
			}
		}
		
		for (Node k : nodes) {
			for (Node i : nodes) {
				double ik = result.get(new NodePair(i, k));
				for (Node j : nodes) {
					double ij = result.get(new NodePair(i, j));					
					double kj = result.get(new NodePair(k, j));
					if (ij > ik + kj) {
						result.put(new NodePair(i, j), ik + kj);
					}
				}
			}
		}

		return result;
	}
}
