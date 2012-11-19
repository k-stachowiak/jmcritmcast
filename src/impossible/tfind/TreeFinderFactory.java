package impossible.tfind;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;

public interface TreeFinderFactory {
	MetricConstrainedSteinerTreeFinder createConstrainedPathAggr(
			ConstrainedPathFinder pathFinder, PathAggregator pathAggregator);
	
	MetricConstrainedSteinerTreeFinder createRdp();

	MetricConstrainedSteinerTreeFinder createHmcmc(
			ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
