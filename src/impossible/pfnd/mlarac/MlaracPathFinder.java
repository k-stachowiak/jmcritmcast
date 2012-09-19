package impossible.pfnd.mlarac;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.LagrangeMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import impossible.pfnd.dkstr.DijkstraPathFinder;
import impossible.pfnd.dkstr.DijkstraRelaxation;

import java.util.ArrayList;
import java.util.List;

public class MlaracPathFinder implements ConstrainedPathFinder {

	private final PathSubstiutor pathSubstitutor;
	private final LambdaEstimator lambdaEstimator;
	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	public MlaracPathFinder(
			PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer) {
		this.pathSubstitutor = pathSubstitutor;
		this.lambdaEstimator = lambdaEstimator;
		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

		// Initialize helpers.
		List<PathFinder> indexDijksrtas = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics(); ++m) {

			MetricProvider metricProvider = new IndexMetricProvider(m);

			DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
					metricProvider);

			indexDijksrtas.add(new DijkstraPathFinder(dijkstraRelaxation));
		}

		// Exceeding path.
		Path exceedingPath = indexDijksrtas.get(0).find(graph, from, to);
		if (exceedingPath == null)
			return null;

		// Immediate success condition.
		if (constraintsComparer.fulfilsAll(exceedingPath, constraints))
			return exceedingPath;

		// Initialize non-exceeding paths; check for the immediate failure
		// condition.
		List<Path> nonExceedingPaths = new ArrayList<>();
		for (int m = 1; m < graph.getNumMetrics(); ++m) {
			Path nonExceedingPath = indexDijksrtas.get(m).find(graph, from, to);
			if (nonExceedingPath == null
					|| !constraintsComparer.fulfilsIndex(nonExceedingPath, m,
							constraints.get(m - 1)))
				return null;
			nonExceedingPaths.add(nonExceedingPath);
		}

		// Approximation iterations.
		boolean done = false;
		int numIterations = 0;
		Path previous = null;
		List<Path> approximations = new ArrayList<>();
		while (!done) {

			// Estiamte lambdas
			List<Double> lambdas = lambdaEstimator.estimate(constraints,
					exceedingPath, nonExceedingPaths);
			if (lambdas == null)
				break;

			PathFinder linearCombinationDijkstra = pathFinderFactory
					.createLinearCombinationDijkstra(1, lambdas);

			// Find candidate.
			Path candidate = linearCombinationDijkstra.find(graph, from, to);
			if (candidate == null) {
				break;
			}

			// Evaluate candidate.
			if (candidate.equals(previous)) {
				break;

			} else {
				previous = candidate;
			}

			if (constraintsComparer.fulfilsAll(candidate, constraints)) {
				approximations.add(candidate);
			}

			if (constraintsComparer.breaksAll(candidate, constraints)) {
				exceedingPath = candidate;

			} else {
				nonExceedingPaths = pathSubstitutor.substitute(candidate,
						nonExceedingPaths, constraints);
			}

			// End condition.
			if (peakReached(nonExceedingPaths, exceedingPath, lambdas, constraints))
				done = true;

			if (++numIterations > 5)
				break;
		}

		// Determine if feasible result was found.
		if (approximations.isEmpty())
			return null;

		return approximations.get(approximations.size() - 1);
	}

	private boolean peakReached(List<Path> nonExceedingPaths,
			Path exceedingPath, List<Double> lambdas, List<Double> constraints) {

		MetricProvider metricProvider = new LagrangeMetricProvider(1,
				constraints, lambdas);

		double previousCost = metricProvider.getPreAdditive(exceedingPath);

		for (Path nonExceedingPath : nonExceedingPaths) {

			double currentCost = metricProvider.getPreAdditive(nonExceedingPath);

			if (Math.abs(currentCost - previousCost) > 0.1)
				return false;

			previousCost = currentCost;
		}

		return true;
	}
}
