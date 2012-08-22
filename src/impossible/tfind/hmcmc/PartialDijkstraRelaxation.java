package impossible.tfind.hmcmc;

import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.pfnd.dkstr.DijkstraRelaxation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartialDijkstraRelaxation extends DijkstraRelaxation {

	private final List<Double> constraints;

	private Map<Node, Double> labels;
	private Map<Node, List<Double>> specifficLabels;

	public PartialDijkstraRelaxation(List<Double> constraints) {
		this.constraints = constraints;
	}

	@Override
	public void reset(Graph graph, Node from) {

		labels = new HashMap<>();
		specifficLabels = new HashMap<>();
		predecessors = new HashMap<>();

		for (Node node : graph.getNodes()) {

			double metric = node.equals(from) ? 0.0 : Double.POSITIVE_INFINITY;

			List<Double> metrics = new ArrayList<>();
			for (int m = 0; m < graph.getNumMetrics(); ++m) {
				metrics.add(metric);
			}

			labels.put(node, metric);
			specifficLabels.put(node, metrics);
			predecessors.put(node, node);
		}
	}

	@Override
	public boolean relax(Graph graph, Node from, Node to) {

		Edge edge = graph.getEdge(from.getId(), to.getId());

		// Find the hypothetical metrics of the new subpath.
		// -------------------------------------------------
		List<Double> aggregated = new ArrayList<>(specifficLabels.get(from));
		for (int i = 0; i < aggregated.size(); ++i)
			aggregated.set(i, aggregated.get(i) + edge.getMetrics().get(i));

		// Find the max term over the set of metrics.
		// ------------------------------------------
		double maximum = Double.NEGATIVE_INFINITY;
		for (int i = 1; i < aggregated.size(); ++i) {
			double term = aggregated.get(i) / constraints.get(i - 1);
			if (term > maximum)
				maximum = term;
		}

		// Relax if condition satisfied.
		// -----------------------------
		if (maximum < labels.get(to)) {
			labels.put(to, maximum);
			predecessors.put(to, from);			
			specifficLabels.put(to, aggregated);
			
			return true;
		}

		return false;
	}

	@Override
	public boolean isCheaper(Node a, Node b) {
		return labels.get(a) < labels.get(b);
	}
}