package impossible.pfnd.hmcp;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.pfnd.dkstr.DijkstraRelaxation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LookAheadHmcpDijkstraRelaxation extends DijkstraRelaxation {

	private final List<Double> constraints;
	private final ReverseHmcpDijkstraRelaxation reverseRelaxation;

	private Map<Node, Double> f;
	private Map<Node, List<Double>> G;

	public LookAheadHmcpDijkstraRelaxation(List<Double> constraints,
			ReverseHmcpDijkstraRelaxation reverseRelaxation) {
		this.constraints = constraints;
		this.reverseRelaxation = reverseRelaxation;
	}

	@Override
	public void reset(Graph graph, Node from) {

		f = new HashMap<>();
		G = new HashMap<>();
		predecessors = new HashMap<>();

		for (Node node : graph.getNodes()) {

			List<Double> metrics = new ArrayList<>();

			if (node.equals(from)) {
				f.put(node, 0.0);
				for (int m = 0; m < graph.getNumMetrics(); ++m)
					metrics.add(0.0);

			} else {
				f.put(node, Double.POSITIVE_INFINITY);
				for (int m = 0; m < graph.getNumMetrics(); ++m)
					metrics.add(Double.POSITIVE_INFINITY);

			}

			G.put(node, metrics);

			predecessors.put(node, node);
		}

	}

	@Override
	public boolean relax(Graph graph, Node from, Node to) {

		Edge edge = graph.getEdge(from.getId(), to.getId());

		// The labels for the "tmp" node
		List<Double> GTmp = new ArrayList<>();
		List<Double> RTmp = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics(); ++m) {
			GTmp.add(0.0);
			RTmp.add(0.0);
		}

		double fTmp = Double.NEGATIVE_INFINITY;

		// Pick the weight that increases the normalized cost the most
		for (int m = 1; m < graph.getNumMetrics(); ++m) {
			double newWeight = (G.get(from).get(m) + edge.getMetrics().get(m))
					/ constraints.get(m - 1);

			if (newWeight > fTmp)
				fTmp = newWeight;
		}

		// Fill the tmp aggregations
		for (int m = 1; m < graph.getNumMetrics(); ++m) {
			GTmp.set(m, G.get(from).get(m) + edge.getMetrics().get(m));
			RTmp.set(m, reverseRelaxation.getRDist(to, m));
		}

		// Check and relax
		List<Double> RTo = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics(); ++m)
			RTo.add(reverseRelaxation.getRDist(to, m));

		if (preferTheBest(from, to, GTmp, RTmp, G.get(to), RTo, fTmp,
				graph.getNumMetrics())) {
			
			f.put(to, fTmp);
			predecessors.put(to, from);

			for (int m = 1; m < graph.getNumMetrics(); ++m) {
				G.get(to).set(m, GTmp.get(m));
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean isCheaper(Node a, Node b) {
		return f.get(a) < f.get(b);
	}

	public boolean constraintsFulfilled(Node to, int numMetrics) {
		for (int m = 1; m < numMetrics; ++m) {
			if (G.get(to).get(m) > constraints.get(m - 1)) {
				return false;
			}
		}
		return true;
	}

	private boolean preferTheBest(Node a, Node b, List<Double> Ga,
			List<Double> Ra, List<Double> Gb, List<Double> Rb, double fa, int M) {

		// Does A fulfill the constraints?
		boolean aFulfills = true;
		for (int m = 1; m < M; ++m)
			if (Ga.get(m) + Ra.get(m) > constraints.get(m - 1)) {
				aFulfills = false;
				break;
			}

		if (aFulfills)
			return true;

		// Does B fulfill the constraints?
		boolean bFulfills = true;
		for (int m = 1; m < M; ++m)
			if (Gb.get(m) + Rb.get(m) > constraints.get(m - 1)) {
				bFulfills = false;
				break;
			}

		if (bFulfills)
			return false;

		// If got here then just compare forward aggregates
		return fa < f.get(b);
	}
}
