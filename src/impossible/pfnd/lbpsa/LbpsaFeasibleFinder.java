package impossible.pfnd.lbpsa;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.metrprov.LinearCombinationMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LbpsaFeasibleFinder implements ConstrainedPathFinder {

	// Strategies
	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	private List<Double> constraints;

	private List<LbpsaFeasibleFinderState> feasibleStates;

	public LbpsaFeasibleFinder(PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer, List<Double> constraints) {

		// Strategies.
		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;

		// ,,Constants''.
		this.constraints = constraints;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		feasibleStates = new ArrayList<>();
		List<Double> lambdas = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics() - 1; ++m)
			lambdas.add(0.0);

		double upperBound = initUb(graph);

		iterationLoop(graph, from, to, upperBound, lambdas);

		if (feasibleStates.isEmpty())
			return null;

		return feasibleStates.get(feasibleStates.size() - 1).getFeasiblePath();
	}

	@Override
	public void setConstraints(List<Double> constraints) {
		this.constraints = new ArrayList<>(constraints);
	}

	public List<Double> getConstraints() {
		return constraints;
	}

	public List<Double> getLambdas() {

		if (feasibleStates.isEmpty())
			return null;

		return feasibleStates.get(feasibleStates.size() - 1).getLambdas();
	}

	public Double getUpperBound() {

		if (feasibleStates.isEmpty())
			return null;

		return feasibleStates.get(feasibleStates.size() - 1).getUpperBound();
	}

	public Map<Node, List<Double>> getSpecifficMetrics() {

		if (feasibleStates.isEmpty())
			return null;

		return feasibleStates.get(feasibleStates.size() - 1).getRelaxation()
				.getLabels();
	}

	private void iterationLoop(Graph graph, Node from, Node to,
			double currentUpperBound, List<Double> currentLambdas) {

		double lk = 2.0;
		double prevL = Double.NaN;

		int iterationsSinceLChange = 0;
		int passes = 0;

		List<Double> result = new ArrayList<>(currentLambdas);

		while (true) {

			// Initialize finder.
			// ------------------
			MetricProvider metricProvider = new LinearCombinationMetricProvider(
					1, currentLambdas);

			DefaultDijkstraRelaxation relaxation = new DefaultDijkstraRelaxation(
					metricProvider);

			PathFinder pathFinder = pathFinderFactory
					.createDijkstra(relaxation);

			// Find and evaluate path.
			// -----------------------
			Path path = pathFinder.find(graph, from, to);

			if (path != null
					&& constraintsComparer.fulfilsAll(path, constraints)
					&& path.getMetrics().get(0) < currentUpperBound) {

				feasibleStates.add(new LbpsaFeasibleFinderState(relaxation,
						currentLambdas, currentUpperBound, path));

			}

			/*
			 * if (path != null && constraintsComparer.fulfilsAll(path,
			 * constraints)) break;
			 */

			// Iteration step.
			// ---------------
			double L = metricProvider.getAdditive(path);

			int metric = getMaxIndex(path);
			double step = computeStep(path, metric, lk, currentUpperBound, L);
			result.set(metric - 1, result.get(metric - 1) + step);
			if (result.get(metric - 1) < 0.0)
				result.set(metric - 1, 0.0);

			// Purple magic.
			// -------------
			if (L == prevL) {
				++iterationsSinceLChange;
			} else {
				iterationsSinceLChange = 0;
				prevL = L;
			}

			if (iterationsSinceLChange >= 10) {
				iterationsSinceLChange = 0;
				lk *= 0.5;
			}

			if (++passes > 100) {
				passes = 0;
				lk *= 0.5;
			}

			if (lk < 0.125)
				break;
		}
	}

	private Double initUb(Graph graph) {
		double sum = 0.0;
		for (Edge edge : graph.getEdges())
			sum += edge.getMetrics().get(0);
		return sum;
	}

	private int getMaxIndex(Path path) {

		int result = -1;
		double max = Double.NEGATIVE_INFINITY;

		for (int m = 1; m < path.getMetrics().size(); ++m) {
			double value = path.getMetrics().get(m) - constraints.get(m - 1);
			if (value > max) {
				max = value;
				result = m;
			}
		}

		return result;
	}

	private double computeStep(Path path, int metric, double lk,
			Double currentUpperBound, double L) {

		double m = path.getMetrics().get(metric);
		double c = constraints.get(metric - 1);
		double denom = (m - c);
		return (lk * (currentUpperBound - L)) / (denom * denom);
	}
}
