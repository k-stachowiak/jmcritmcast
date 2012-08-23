package impossible.pfnd;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import impossible.pfnd.dkstr.DijkstraPathFinder;
import impossible.pfnd.dkstr.DijkstraRelaxation;
import impossible.pfnd.hmcp.HmcpPathFinder;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.LinearCombinationMetricProvider;
import impossible.pfnd.mlarac.MlaracPathFinder;
import impossible.pfnd.mlarac.PathSubstiutor;

import java.util.List;

public class PathFinderFactoryImpl implements PathFinderFactory {

	@Override
	public PathFinder createDijkstra(DijkstraRelaxation dijkstraRelaxation) {
		return new DijkstraPathFinder(dijkstraRelaxation);
	}

	@Override
	public PathFinder createDijkstraIndex(int metricIndex) {

		MetricProvider metricProvider = new IndexMetricProvider(metricIndex);

		DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
				metricProvider);

		return new DijkstraPathFinder(dijkstraRelaxation);
	}

	@Override
	public PathFinder createLinearCombinationDijkstra(int offset,
			List<Double> constraints, List<Double> lambdas) {

		MetricProvider metricProvider = new LinearCombinationMetricProvider(
				offset, constraints, lambdas);
		DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
				metricProvider);
		return new DijkstraPathFinder(dijkstraRelaxation);
	}

	@Override
	public PathFinder createHmcp(List<Double> constraints) {
		return new HmcpPathFinder(this, constraints);
	}

	@Override
	public PathFinder createMlarac(List<Double> constraints,
			PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			ConstraintsComparer constraintsComparer) {
		return new MlaracPathFinder(constraints, pathSubstitutor,
				lambdaEstimator, this, constraintsComparer);
	}
}
