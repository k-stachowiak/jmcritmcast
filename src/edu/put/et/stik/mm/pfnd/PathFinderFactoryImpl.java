package edu.put.et.stik.mm.pfnd;


import java.util.List;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.helpers.metrprov.IndexMetricProvider;
import edu.put.et.stik.mm.helpers.metrprov.LinearCombinationMetricProvider;
import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.pfnd.dkstr.DijkstraPathFinder;
import edu.put.et.stik.mm.pfnd.hmcop.HmcopPathFinder;
import edu.put.et.stik.mm.pfnd.hmcp.HmcpPathFinder;
import edu.put.et.stik.mm.pfnd.lbpsa.LbpsaPathFinder;
import edu.put.et.stik.mm.pfnd.mlarac.LambdaEstimator;
import edu.put.et.stik.mm.pfnd.mlarac.MlaracPathFinder;
import edu.put.et.stik.mm.pfnd.mlarac.PathSubstiutor;

public class PathFinderFactoryImpl implements PathFinderFactory {

	@Override
	public PathFinder createDijkstra(CommonRelaxation commonRelaxation) {
		return new DijkstraPathFinder(commonRelaxation);
	}

	@Override
	public PathFinder createDijkstraIndex(int metricIndex) {

		MetricProvider metricProvider = new IndexMetricProvider(metricIndex);

		CommonRelaxation commonRelaxation = new CommonRelaxationImpl(
				metricProvider);

		return new DijkstraPathFinder(commonRelaxation);
	}

	@Override
	public PathFinder createLinearCombinationDijkstra(int offset, List<Double> lambdas) {

		MetricProvider metricProvider = new LinearCombinationMetricProvider(
				offset, lambdas);
		CommonRelaxation commonRelaxation = new CommonRelaxationImpl(
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
