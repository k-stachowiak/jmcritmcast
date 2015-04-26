package pfnd;


import helpers.ConstraintsComparer;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.LinearCombinationMetricProvider;
import helpers.metrprov.MetricProvider;

import java.util.List;

import pfnd.dkstr.DijkstraPathFinder;
import pfnd.hmcop.HmcopPathFinder;
import pfnd.hmcp.HmcpPathFinder;
import pfnd.lbpsa.LbpsaPathFinder;
import pfnd.mlarac.LambdaEstimator;
import pfnd.mlarac.MlaracPathFinder;
import pfnd.mlarac.PathSubstiutor;


public class PathFinderFactoryImpl implements PathFinderFactory {

	@Override
	public PathFinder createDijkstra(Relaxation commonRelaxation) {
		return new DijkstraPathFinder(commonRelaxation);
	}

	@Override
	public PathFinder createDijkstraIndex(int metricIndex) {

		MetricProvider metricProvider = new IndexMetricProvider(metricIndex);

		Relaxation commonRelaxation = new MetricRelaxation(
				metricProvider);

		return new DijkstraPathFinder(commonRelaxation);
	}

	@Override
	public PathFinder createLinearCombinationDijkstra(int offset, List<Double> lambdas) {

		MetricProvider metricProvider = new LinearCombinationMetricProvider(
				offset, lambdas);
		Relaxation commonRelaxation = new MetricRelaxation(
				metricProvider);
		return new DijkstraPathFinder(commonRelaxation);
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
