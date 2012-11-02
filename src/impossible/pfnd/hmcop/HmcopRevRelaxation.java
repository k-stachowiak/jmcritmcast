package impossible.pfnd.hmcop;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.util.NodeComparator;
import impossible.pfnd.CommonRelaxation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HmcopRevRelaxation extends CommonRelaxation {

	// Constants.
	private final List<Double> constraints;

	// State
	private Map<Node, Double> r;
	private Map<Node, List<Double>> R; // Indexing: R[node][metric]

	/*
	 * private String stateString() {
	 * 
	 * StringBuilder stringBuilder = new StringBuilder();
	 * 
	 * // Aggregated metrics stringBuilder.append("r\t"); for (Map.Entry<Node,
	 * Double> entry : r.entrySet()) { String value = String.format("%1$.2f",
	 * entry.getValue()); stringBuilder.append(value + "\t"); }
	 * stringBuilder.append("\n");
	 * 
	 * // Specific metrics stringBuilder.append("R\t"); for (Map.Entry<Node,
	 * List<Double>> entry : R.entrySet()) { stringBuilder.append("("); for
	 * (Double metric : entry.getValue()) { String metricString =
	 * String.format("%1$.2f", metric); stringBuilder.append(metricString +
	 * " "); } stringBuilder.append(")"); } stringBuilder.append("\n");
	 * 
	 * // Predecessors stringBuilder.append(predecessorsString());
	 * 
	 * return stringBuilder.toString(); }
	 */

	public HmcopRevRelaxation(List<Double> constraints) {
		this.constraints = constraints;
	}

	@Override
	public void reset(Graph graph, Node from) {

		// Helper.
		int numMetrics = graph.getNumMetrics();

		// Reallocate state.
		r = new TreeMap<>(new NodeComparator());
		R = new TreeMap<>(new NodeComparator());
		predecessors = new TreeMap<>(new NodeComparator());

		// Set initial labels.
		for (Node node : graph.getNodes()) {

			List<Double> infMetrics = new ArrayList<>();
			for (int i = 0; i < numMetrics; ++i) {
				infMetrics.add(Double.POSITIVE_INFINITY);
			}

			r.put(node, Double.POSITIVE_INFINITY);
			R.put(node, infMetrics);
			predecessors.put(node, node);
		}

		// Special values for initial node.
		List<Double> zeroMetrics = new ArrayList<>();
		for (int i = 0; i < numMetrics; ++i) {
			zeroMetrics.add(0.0);
		}

		r.put(from, 0.0);
		R.put(from, zeroMetrics);
	}

	@Override
	public boolean relax(Graph graph, Node from, Node to) {

		// Helpers.
		int numMetrics = graph.getNumMetrics();
		Edge edge = graph.getEdge(from.getId(), to.getId());

		// Aggregated candidate cost.
		double aggregatedCandidate = 0.0;
		for (int m = 1; m < numMetrics; ++m) {
			double term = (R.get(from).get(m) + edge.getMetrics().get(m))
					/ constraints.get(m - 1);
			aggregatedCandidate += term;
		}

		if (aggregatedCandidate < r.get(to)) {

			r.put(to, aggregatedCandidate);
			predecessors.put(to, from);

			for (int m = 0; m < numMetrics; ++m) {
				R.get(to).set(m, R.get(from).get(m) + edge.getMetrics().get(m));
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean isCheaper(Node a, Node b) {
		return r.get(a) < r.get(b);
	}

	public double getR(Node node, int metric) {
		return R.get(node).get(metric);
	}

	public boolean failureCondition(Node source, double numMetrics) {
		return r.get(source) > numMetrics;
	}
}
