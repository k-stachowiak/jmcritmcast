package impossible.pfnd.lbpsa;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import impossible.pfnd.mlarac.LinearCombinationMetricProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LbpsaFeasibleFinder implements ConstrainedPathFinder {

	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	private List<Double> constraints;
	private List<Double> lambdas;

	private Double upperBound;

	private Map<Node, List<Double>> specifficMetrics;
	private DefaultDijkstraRelaxation currentRelaxation;

	public LbpsaFeasibleFinder(PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer, List<Double> constraints) {

		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;
		this.constraints = constraints;

		lambdas = null;
		upperBound = 0.0;
		specifficMetrics = new HashMap<>();
		currentRelaxation = null;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

		List<Double> currentLambdas = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics() - 1; ++m)
			currentLambdas.add(0.0);

		upperBound = initUb(graph);

		currentLambdas = iterationLoop(graph, from, to, currentLambdas);

		PathFinder pathFinder = initFinder(1, currentLambdas);

		Path path = pathFinder.find(graph, from, to);
		if (constraintsComparer.fulfilsAll(path, constraints)) {
			
			MetricProvider metricProvider = new LinearCombinationMetricProvider(
					1, currentLambdas);
			
			upperBound = metricProvider.getAdditive(path);
			
			specifficMetrics = new HashMap<>(currentRelaxation.getLabels());
			lambdas = currentLambdas;

			{
				
				System.out.println("Lower bound = "
						+ metricProvider.getAdditive(path) + ", Upper bound = "
						+ upperBound);
			}

			return path;
		}
		return null;
	}

	@Override
	public void setConstraints(List<Double> constraints) {
		constraints = new ArrayList<>(constraints);

	}

	public List<Double> getConstraints() {
		return constraints;
	}

	public List<Double> getLambdas() {
		return lambdas;
	}

	public Double getUpperBound() {
		return upperBound;
	}

	public Map<Node, List<Double>> getSpecifficMetrics() {
		return specifficMetrics;
	}

	private Double initUb(Graph graph) {
		double sum = 0.0;
		for (Edge edge : graph.getEdges())
			sum += edge.getMetrics().get(0);
		return sum;
	}

	private PathFinder initFinder(int i, List<Double> currentLambdas) {

		MetricProvider metricProvider = new LinearCombinationMetricProvider(1,
				currentLambdas);
		currentRelaxation = new DefaultDijkstraRelaxation(metricProvider);
		return pathFinderFactory.createDijkstra(currentRelaxation);
	}

	private List<Double> iterationLoop(Graph graph, Node from, Node to,
			List<Double> currentLambdas) {

		double lk = 2.0;
		double prevL = Double.NaN;

		int iterationsSinceLChange = 0;
		int passes = 0;

		List<Double> result = new ArrayList<>(currentLambdas);

		while (true) {
			PathFinder pathFinder = initFinder(1, result);
			Path path = pathFinder.find(graph, from, to);

			/*
			if (constraintsComparer.fulfilsAll(path, constraints)
					&& path.getMetrics().get(0) < upperBound)
				upperBound = path.getMetrics().get(0);
				*/
			
			
			if(path != null && constraintsComparer.fulfilsAll(path, constraints))
				break;
				

			MetricProvider metricProvider = new LinearCombinationMetricProvider(
					1, result);

			double L = metricProvider.getAdditive(path);
			int metric = getMaxIndex(path);

			double step = computeStep(path, metric, lk, upperBound, L);

			result.set(metric - 1, result.get(metric - 1) + step);
			if (result.get(metric - 1) < 0.0)
				result.set(metric - 1, 0.0);

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

		return result;
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
