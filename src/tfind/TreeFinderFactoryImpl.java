package tfind;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import tfind.hmcmc.HmcmcTreeFinder;
import tfind.paggr.ConstrainedPathAggrTreeFinder;
import tfind.prim.PrimTreeFinder;
import tfind.rdp.RdpQuasiExact;
import helpers.ConstraintsComparer;
import helpers.PathAggregator;
import helpers.metrprov.MetricProvider;

public class TreeFinderFactoryImpl implements TreeFinderFactory {

	@Override
	public ConstrainedSteinerTreeFinder createConstrainedPathAggr(
			ConstrainedPathFinder pathFinder, PathAggregator pathAggregator) {

		return new ConstrainedPathAggrTreeFinder(pathFinder, pathAggregator);
	}

	@Override
	public ConstrainedSteinerTreeFinder createRdpQuasiExact(
			ConstraintsComparer constraintsComparer) {
		return new RdpQuasiExact(constraintsComparer);
	}
	
	@Override
	public ConstrainedSteinerTreeFinder createRdpHeuristic(
			ConstraintsComparer constraintsComparer) {
		return new RdpQuasiExact(constraintsComparer);
	}

	@Override
	public ConstrainedSteinerTreeFinder createHmcmc(
			ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator) {
		return new HmcmcTreeFinder(constraintsComparer, pathFinderFactory,
				pathAggregator);
	}

	@Override
	public SpanningTreeFinder createPrim(MetricProvider metricProvider) {
		return new PrimTreeFinder(metricProvider);
	}

}
