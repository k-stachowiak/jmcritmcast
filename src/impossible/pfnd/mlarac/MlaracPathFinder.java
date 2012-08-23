package impossible.pfnd.mlarac;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.TopologyDebug;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import impossible.pfnd.dkstr.DijkstraPathFinder;
import impossible.pfnd.dkstr.DijkstraRelaxation;

import java.util.ArrayList;
import java.util.List;

public class MlaracPathFinder implements PathFinder {

	private final List<Double> constraints;
	private final PathSubstiutor pathSubstitutor;
	private final LambdaEstimator lambdaEstimator;
	private final PathFinderFactory pathFinderFactory;
	private final ConstraintsComparer constraintsComparer;

	public MlaracPathFinder(List<Double> constraints,
			PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			PathFinderFactory pathFinderFactory,
			ConstraintsComparer constraintsComparer) {
		this.constraints = constraints;
		this.pathSubstitutor = pathSubstitutor;
		this.lambdaEstimator = lambdaEstimator;
		this.pathFinderFactory = pathFinderFactory;
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public Path find(Graph graph, Node from, Node to) {

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
		Path previous = null;
		List<Path> approximations = new ArrayList<>();
		while (!done) {

			// Estiamte lambdas
			List<Double> lambdas = lambdaEstimator.estimate(constraints,
					exceedingPath, nonExceedingPaths);

			PathFinder linearCombinationDijkstra = pathFinderFactory
					.createLinearCombinationDijkstra(1, constraints, lambdas);

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
			if (peakReached(nonExceedingPaths, exceedingPath, lambdas)) {
				done = true;
			}
		}

		// Determine if feasible result was found.
		if (approximations.isEmpty())
			return null;

		return approximations.get(approximations.size() - 1);
	}

	private boolean peakReached(List<Path> nonExceedingPaths,
			Path exceedingPath, List<Double> lambdas) {

		MetricProvider linearCombinationMetricProvider = new LinearCombinationMetricProvider(
				1, lambdas, constraints);

		double previousCost = linearCombinationMetricProvider
				.getAdditive(exceedingPath);

		for (Path nonExceedingPath : nonExceedingPaths) {

			double currentCost = linearCombinationMetricProvider
					.getAdditive(nonExceedingPath);

			if (Math.abs(currentCost - previousCost) > 0.1)
				return false;

			previousCost = currentCost;
		}

		return true;
	}
}
