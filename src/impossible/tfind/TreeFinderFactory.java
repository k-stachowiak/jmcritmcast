package impossible.tfind;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;

import java.util.List;

public interface TreeFinderFactory {
	SteinerTreeFinder createPathAggr(List<Double> constraints,
			PathFinder pathFinder, ConstraintsComparer constraintsComparer,
			PathAggregator pathAggregator);
	
	SteinerTreeFinder createHmcmc(
			ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator,
			List<Double> constraints);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
