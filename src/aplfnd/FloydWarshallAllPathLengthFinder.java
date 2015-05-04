package aplfnd;

import helpers.PathMetric;
import helpers.metrprov.MetricProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodePair;

public class FloydWarshallAllPathLengthFinder implements AllPathLengthFinder {

	@Override
	public Map<NodePair, PathMetric> find(Graph graph,
			MetricProvider metricProvider) {

		List<Node> nodes = graph.getNodes();

		Map<NodePair, PathMetric> result = new HashMap<>();

		for (Node u : nodes) {
			for (Node v : nodes) {
				if (u.equals(v)) {
					result.put(new NodePair(u, v), new PathMetric(0, 0.0));
				} else {
					Edge e = graph.getEdge(u.getId(), v.getId());
					if (e == null) {
						result.put(new NodePair(u, v), new PathMetric(0,
								Double.POSITIVE_INFINITY));
					} else {
						result.put(new NodePair(u, v), new PathMetric(1,
								metricProvider.get(e)));
					}
				}
			}
		}

		for (Node k : nodes) {
			for (Node i : nodes) {
				PathMetric ik = result.get(new NodePair(i, k));

				for (Node j : nodes) {
					PathMetric ij = result.get(new NodePair(i, j));
					PathMetric kj = result.get(new NodePair(k, j));

					if (ij.getCost() > ik.getCost() + kj.getCost()) {
						result.put(new NodePair(i, j),
								new PathMetric(ij.getHop() + 1, ik.getCost()
										+ kj.getCost()));
					}

				}

			}
		}

		return result;
	}
}
