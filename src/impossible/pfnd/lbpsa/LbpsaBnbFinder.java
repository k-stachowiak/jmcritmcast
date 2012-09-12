package impossible.pfnd.lbpsa;

import impossible.helpers.metrprov.LagrangeMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.PathFinder;

import java.util.ArrayList;
import java.util.List;

public class LbpsaBnbFinder implements PathFinder {

	private final LbpsaFeasibleFinder feasibleFinder;
	private final MetricProvider feasibleMetricProvider;
	private final List<Double> constraints;

	private List<Integer> currentPath;
	private List<Double> specifficCosts;
	private List<List<Integer>> feasiblePaths;

	public LbpsaBnbFinder(LbpsaFeasibleFinder feasibleFinder, List<Double> constraints) {
		this.feasibleFinder = feasibleFinder;
		this.constraints = new ArrayList<>(constraints);
		feasibleMetricProvider = new LagrangeMetricProvider(1,
				constraints, feasibleFinder.getLambdas());
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		currentPath = new ArrayList<>();
		currentPath.add(to.getId());

		specifficCosts = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics(); ++m)
			specifficCosts.add(0.0);

		feasiblePaths = new ArrayList<>();

		findRecursively(graph, from, to);

		if (feasiblePaths.isEmpty())
			return null;

		return cheapestFeasible(graph);
	}

	private void findRecursively(Graph graph, Node from, Node to) {

		int currentNodeId = currentPath.get(currentPath.size() - 1);
		Node currentNode = graph.getNode(currentNodeId);
		if (currentNode.equals(from)) {
			feasiblePaths.add(new ArrayList<>(currentPath));
			return;
		}

		for (Node neighbor : graph.getNeighbors(currentNode)) {

			if (currentPath.contains(neighbor.getId()))
				continue;

			Edge edge = graph.getEdge(currentNode.getId(), neighbor.getId());

			for (int m = 0; m < graph.getNumMetrics(); ++m)
				specifficCosts.set(m, specifficCosts.get(m)
						+ edge.getMetrics().get(m));

			if (checkConditions(neighbor, edge, graph.getNumMetrics())) {
				currentPath.add(neighbor.getId());
				findRecursively(graph, from, to);
				currentPath.remove(currentPath.size() - 1);
			}

			for (int m = 0; m < graph.getNumMetrics(); ++m)
				specifficCosts.set(m, specifficCosts.get(m)
						- edge.getMetrics().get(m));
		}
	}

	private boolean checkConditions(Node to, Edge edge, int numMetrics) {

		// Helpers.
		// --------

		// Dummy edges.
		Edge fwdEdge = new Edge(0, 0, specifficCosts);
		Edge revEdge = new Edge(0, 0, feasibleFinder.getSpecifficMetrics().get(
				to));

		// Labels.
		double aggrSoFar = feasibleMetricProvider.get(fwdEdge)
				+ feasibleMetricProvider.get(edge);

		double aggrReverse = feasibleMetricProvider.get(revEdge);

		// Other.
		double upperBound = feasibleFinder.getUpperBound();

		double weightedConstraints = 0.0;
		for (int m = 1; m < numMetrics; ++m) {
			weightedConstraints += feasibleFinder.getLambdas().get(m - 1)
					* constraints.get(m - 1);
		}

		// Conditions.
		// -----------
		// Condition 3:
		boolean cnd3 = aggrSoFar + aggrReverse < upperBound
				+ weightedConstraints;

		if (!cnd3)
			return false;

		// Condition 1:
		boolean cnd1 = fwdEdge.getMetrics().get(0)
				+ revEdge.getMetrics().get(0) <= upperBound;

		if (!cnd1)
			return false;

		// Condition 2:
		boolean cnd2 = true;
		for (int i = 1; i < numMetrics; ++i) {
			double value = fwdEdge.getMetrics().get(i)
					+ revEdge.getMetrics().get(i)
					- constraints.get(i - 1);

			if (value > 0.0) {
				cnd2 = false;
				break;
			}
		}

		return cnd2;
	}

	private Path cheapestFeasible(Graph graph) {

		Path cheapest = null;
		double cheapestCost = Double.POSITIVE_INFINITY;

		for (List<Integer> pathNodes : feasiblePaths) {

			Path candidate = new Path(graph, pathNodes);
			double candidateCost = candidate.getMetrics().get(0);

			if (candidateCost < cheapestCost) {
				cheapestCost = candidateCost;
				cheapest = candidate;
			}
		}

		return cheapest;
	}
}
