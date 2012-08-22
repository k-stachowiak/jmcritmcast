package impossible.tfind;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.tfind.hmcmc.HmcmcTreeFinder;
import impossible.tfind.paggr.PathAggrTreeFinder;
import impossible.tfind.prim.PrimTreeFinder;

import java.util.List;

public class TreeFinderFactoryImpl implements TreeFinderFactory {

	@Override
	public SteinerTreeFinder createPathAggr(List<Double> constraints,
			PathFinder pathFinder, ConstraintsComparer constraintsComparer,
			PathAggregator pathAggregator) {

		return new PathAggrTreeFinder(constraints, pathFinder,
				constraintsComparer, pathAggregator);
	}

	@Override
	public SteinerTreeFinder createHmcmc(
			ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator,
			List<Double> constraints) {
		return new HmcmcTreeFinder(constraintsComparer, pathFinderFactory,
				pathAggregator, constraints);
	}

	@Override
	public SpanningTreeFinder createPrim(MetricProvider metricProvider) {
		return new PrimTreeFinder(metricProvider);
	}

}
