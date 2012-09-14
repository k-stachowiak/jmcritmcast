package impossible.pfnd.hmcop;

import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.pfnd.dkstr.DijkstraRelaxation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HmcopLaRelaxation extends DijkstraRelaxation {

	// Constants.
	private final HmcopRevRelaxation revRelaxation;
	private final List<Double> constraints;
	private final double lambda;

	// State
	private Map<Node, Double> f;
	private Map<Node, List<Double>> G; // Indexing: G[node][metric]

	public HmcopLaRelaxation(HmcopRevRelaxation revRelaxation,
			List<Double> constraints, double lambda) {
		this.revRelaxation = revRelaxation;
		this.constraints = constraints;
		this.lambda = lambda;
	}

	@Override
	public void reset(Graph graph, Node from) {
		// Helper.
		int numMetrics = graph.getNumMetrics();

		// Reallocate state.
		f = new HashMap<>();
		G = new HashMap<>();
		predecessors = new HashMap<>();

		// Set initial labels.
		for (Node node : graph.getNodes()) {

			List<Double> infMetrics = new ArrayList<>();
			for (int i = 0; i < numMetrics; ++i) {
				infMetrics.add(Double.POSITIVE_INFINITY);
			}

			f.put(node, Double.POSITIVE_INFINITY);
			G.put(node, infMetrics);
			predecessors.put(node, node);
		}

		// Special values for initial node.
		List<Double> zeroMetrics = new ArrayList<>();
		for (int i = 0; i < numMetrics; ++i) {
			zeroMetrics.add(0.0);
		}

		f.put(from, 0.0);
		G.put(from, zeroMetrics);

	}

	@Override
	public boolean relax(Graph graph, Node from, Node to) {

		// Helpers.
		int numMetrics = graph.getNumMetrics();
		Edge edge = graph.getEdge(from.getId(), to.getId());

		// Aggregate candidate cost.
		double aggregatedCandidate = computeAggregatedCandidate(from, to, edge,
				numMetrics);

		// Compute specific costs.
		List<Double> GkTo = new ArrayList<>(G.get(to));
		List<Double> GkCand = new ArrayList<>(G.get(from));
		for (int m = 0; m < numMetrics; ++m) {
			GkCand.set(m, GkCand.get(m) + edge.getMetrics().get(m));
		}

		List<Double> RkTo = new ArrayList<>();
		for (int m = 0; m < numMetrics; ++m) {
			RkTo.add(revRelaxation.getR(to, m));
		}
		List<Double> RkCand = new ArrayList<>(RkTo);

		// Relax.
		if (preferTheBest(from, to, GkCand, RkCand, GkTo, RkTo,
				aggregatedCandidate, numMetrics)) {

			f.put(to, aggregatedCandidate);
			G.put(to, GkCand);
			predecessors.put(to, from);

			return true;
		}

		return false;
	}

	@Override
	public boolean isCheaper(Node a, Node b) {
		return f.get(a) < f.get(b);
	}

	public boolean failureCondition(Node destination, int numMetrics) {
		for (int m = 1; m < numMetrics; ++m) {
			if (G.get(destination).get(m) > constraints.get(m - 1)) {
				return false;
			}
		}
		return true;
	}

	private double computeAggregatedCandidate(Node from, Node to, Edge edge,
			int numMetrics) {

		double result;

		if (lambda < Double.POSITIVE_INFINITY) {

			result = 0.0;
			for (int m = 1; m < numMetrics; ++m) {
				double gk = G.get(from).get(m);
				double rk = revRelaxation.getR(to, m);
				double term = (gk + edge.getMetrics().get(m) + rk)
						/ constraints.get(m - 1);
				result += term;
			}

		} else {

			result = Double.NEGATIVE_INFINITY;
			for (int m = 1; m < numMetrics; ++m) {
				double gk = G.get(from).get(m);
				double rk = revRelaxation.getR(to, m);
				double term = (gk + edge.getMetrics().get(m) + rk)
						/ constraints.get(m - 1);
				if (term > result) {
					result = term;
				}
			}
		}

		return result;
	}

	private boolean preferTheBest(Node a, Node b, List<Double> GkA,
			List<Double> RkA, List<Double> GkB, List<Double> RkB,
			double aggregatedCandidate, int numMetrics) {

		if (G.get(a).get(0) < G.get(b).get(0)) {
			boolean allFulfill = true;
			for (int m = 1; m < numMetrics; ++m) {
				if (GkA.get(m) + RkA.get(m) > constraints.get(m - 1)) {
					allFulfill = false;
					break;
				}
			}
			if (allFulfill) {
				return true;
			}
		}

		if (G.get(a).get(0) > G.get(b).get(0)) {
			boolean allFulfill = true;
			for (int m = 1; m < numMetrics; ++m) {
				if (GkB.get(m) + RkB.get(m) > constraints.get(m - 1)) {
					allFulfill = false;
					break;
				}
			}
			if (allFulfill) {
				return false;
			}
		}

		if (aggregatedCandidate < f.get(b)) {
			return true;
		}

		return false;
	}
}
