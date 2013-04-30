package pfnd.lbpsa;


import helpers.ConstraintsComparer;
import helpers.metrprov.LagrangeMetricProvider;
import helpers.metrprov.MetricProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;

import pfnd.CommonRelaxationImpl;
import pfnd.ConstrainedPathFinder;
import pfnd.PathFinder;
import pfnd.PathFinderFactory;


public class LbpsaFeasibleFinder implements ConstrainedPathFinder {

	// Strategies
	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	// State
	private CommonRelaxationImpl relaxation;
	private List<Double> lambdas;
	private double upperBound;

	/*
	 * private String stateString() {
	 * 
	 * StringBuilder stringBuilder = new StringBuilder();
	 * 
	 * // Record labels. if (relaxation != null) {
	 * stringBuilder.append("Labels : "); for (Map.Entry<Node, List<Double>>
	 * entry : relaxation.getLabels() .entrySet()) { stringBuilder.append("(");
	 * for (Double label : entry.getValue()) { stringBuilder.append(label +
	 * " "); } stringBuilder.append(") "); } stringBuilder.append("\n"); }
	 * 
	 * // Record lambdas. stringBuilder.append("Lambdas : "); for (Double lambda
	 * : lambdas) { stringBuilder.append(lambda + " "); }
	 * stringBuilder.append("\n");
	 * 
	 * // Record upper bound. stringBuilder.append("Upper bound : " +
	 * upperBound);
	 * 
	 * return stringBuilder.toString(); }
	 */

	public LbpsaFeasibleFinder(PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer) {

		// Strategies.
		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		lambdas = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics() - 1; ++m) {
			lambdas.add(0.0);
		}

		upperBound = initUb(graph);

		Path feasiblePath = iterationLoop(graph, from, to, constraints);

		return feasiblePath;
	}

	public List<Double> getLambdas() {
		return lambdas;
	}

	public Double getUpperBound() {
		return upperBound;
	}

	public Map<Node, List<Double>> getSpecifficMetrics() {
		return relaxation.getLabels();
	}

	private Path iterationLoop(Graph graph, Node from, Node to,
			List<Double> constraints) {

		double lk = 2.0;
		double prevL = Double.NaN;
		int iterationsSinceLChange = 0;
		int passes = 0;

		while (true) {

			// Initialize finder.
			// ------------------
			MetricProvider metricProvider = new LagrangeMetricProvider(1,
					constraints, lambdas);

			relaxation = new CommonRelaxationImpl(metricProvider);
			PathFinder pathFinder = pathFinderFactory
					.createDijkstra(relaxation);

			// Find and evaluate path.
			// -----------------------
			Path path = pathFinder.find(graph, from, to);

			if (path != null
					&& constraintsComparer.fulfilsAll(path, constraints)) {
				upperBound = path.getMetrics().get(0);
				return path;
			}

			// Iteration step.
			// ---------------
			double L = metricProvider.getPreAdditive(path);
			int metric = getMaxIndex(path, constraints);
			double step = computeStep(path, metric, lk, L, constraints);

			lambdas.set(metric - 1, lambdas.get(metric - 1) + step);
			if (lambdas.get(metric - 1) < 0.0) {
				lambdas.set(metric - 1, 0.0);
			}

			// Purple magic.
			// -------------
			if (L == prevL) {
				++iterationsSinceLChange;
			} else {
				iterationsSinceLChange = 0;
				prevL = L;
			}

			if (iterationsSinceLChange >= 50) {
				iterationsSinceLChange = 0;
				lk *= 0.5;
			}

			if (++passes > 50) {
				passes = 0;
				lk *= 0.5;
			}

			if (lk < 0.125) {
				break;
			}
		}

		return null;
	}

	private Double initUb(Graph graph) {
		double sum = 0.0;
		for (Edge edge : graph.getEdges()) {
			sum += edge.getMetrics().get(0);
		}
		return sum;
	}

	private int getMaxIndex(Path path, List<Double> constraints) {

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

	private double computeStep(Path path, int metric, double lk, double L,
			List<Double> constraints) {

		double m = path.getMetrics().get(metric);
		double c = constraints.get(metric - 1);
		double denom = (m - c);

		if (denom == 0.0) {
			return upperBound * 0.01; // WAT
		}

		return (lk * (upperBound - L)) / (denom * denom);
	}
}
