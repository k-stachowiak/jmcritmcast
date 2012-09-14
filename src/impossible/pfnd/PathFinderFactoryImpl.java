package impossible.pfnd;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.LinearCombinationMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import impossible.pfnd.dkstr.DijkstraPathFinder;
import impossible.pfnd.dkstr.DijkstraRelaxation;
import impossible.pfnd.hmcop.HmcopPathFinder;
import impossible.pfnd.hmcp.HmcpPathFinder;
import impossible.pfnd.lbpsa.LbpsaPathFinder;
import impossible.pfnd.mlarac.LambdaEstimator;
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
	public PathFinder createLinearCombinationDijkstra(int offset, List<Double> lambdas) {

		MetricProvider metricProvider = new LinearCombinationMetricProvider(
				offset, lambdas);
		DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
				metricProvider);
		return new DijkstraPathFinder(dijkstraRelaxation);
	}

	@Override
	public ConstrainedPathFinder createHmcp() {
		return new HmcpPathFinder(this);
	}

	@Override
	public ConstrainedPathFinder createMlarac(PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			ConstraintsComparer constraintsComparer) {
		return new MlaracPathFinder(pathSubstitutor,
				lambdaEstimator, this, constraintsComparer);
	}

	@Override
	public ConstrainedPathFinder createLbpsa(
			ConstraintsComparer constraintsComparer) {
		return new LbpsaPathFinder(this, constraintsComparer);
	}
	
	@Override
	public ConstrainedPathFinder createHmcop(double lambda) {
		return new HmcopPathFinder(lambda);
	}
}
