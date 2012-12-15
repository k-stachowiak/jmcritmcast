package impossible.tfind;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.tfind.hmcmc.HmcmcTreeFinder;
import impossible.tfind.paggr.ConstrainedPathAggrTreeFinder;
import impossible.tfind.prim.PrimTreeFinder;
import impossible.tfind.rdp.RdpTreeFinder;

public class TreeFinderFactoryImpl implements TreeFinderFactory {

	@Override
	public MetricConstrainedSteinerTreeFinder createConstrainedPathAggr(
			ConstrainedPathFinder pathFinder, PathAggregator pathAggregator) {

		return new ConstrainedPathAggrTreeFinder(pathFinder, pathAggregator);
	}

	@Override
	public MetricConstrainedSteinerTreeFinder createRdp(
			ConstraintsComparer constraintsComparer) {
		return new RdpTreeFinder(constraintsComparer);
	}

	@Override
	public MetricConstrainedSteinerTreeFinder createHmcmc(
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
