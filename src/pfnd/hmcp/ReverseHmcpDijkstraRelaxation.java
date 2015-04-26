package pfnd.hmcp;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.NodeComparator;
import pfnd.CommonRelaxation;


public class ReverseHmcpDijkstraRelaxation extends CommonRelaxation {

	private final List<Double> constraints;

	private Map<Node, Double> r;
	private Map<Node, List<Double>> R;

	public ReverseHmcpDijkstraRelaxation(List<Double> constraints) {
		this.constraints = constraints;
	}

	@Override
	public void reset(Graph graph, Node from) {

		r = new TreeMap<>(new NodeComparator());
		R = new TreeMap<>(new NodeComparator());
		predecessors = new TreeMap<>(new NodeComparator());

		for (Node node : graph.getNodes()) {

			List<Double> metrics = new ArrayList<>();

			if (node.equals(from)) {
				r.put(node, 0.0);
				for (int m = 0; m < graph.getNumMetrics(); ++m)
					metrics.add(0.0);

			} else {
				r.put(node, Double.POSITIVE_INFINITY);
				for (int m = 0; m < graph.getNumMetrics(); ++m)
					metrics.add(Double.POSITIVE_INFINITY);

			}

			R.put(node, metrics);

			predecessors.put(node, node);
		}
	}

	@Override
	public boolean relax(Graph graph, Node from, Node to) {

		Edge edge = graph.getEdge(from.getId(), to.getId());

		// Pick the weight that increases the normalized cost the most
		double maxWeight = Double.NEGATIVE_INFINITY;
		for (int m = 1; m < graph.getNumMetrics(); ++m) {

			double newWeight = (R.get(from).get(m) + edge.getMetrics().get(m))
					/ constraints.get(m - 1);

			if (newWeight > maxWeight)
				maxWeight = newWeight;
		}

		// Check and relax
		if (r.get(to) > maxWeight) {

			r.put(to, maxWeight);
			predecessors.put(to, from);

			for (int m = 1; m < graph.getNumMetrics(); ++m)
				R.get(to).set(m, R.get(from).get(m) + edge.getMetrics().get(m));

			return true;
		}

		return false;
	}

	@Override
	public boolean isCheaper(Node a, Node b) {
		return r.get(a) < r.get(b);
	}

	public boolean guaranteedFailure(Node from, int numMetrics) {
		return r.get(from) > numMetrics;
	}

	public double getRDist(Node node, int metric) {
		return R.get(node).get(metric);
	}
}
