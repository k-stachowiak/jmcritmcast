package tfind;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import helpers.ConstraintsComparer;
import helpers.PathAggregator;
import helpers.metrprov.MetricProvider;

public interface TreeFinderFactory {
	ConstrainedSteinerTreeFinder createConstrainedPathAggr(
			ConstrainedPathFinder pathFinder, PathAggregator pathAggregator);

	ConstrainedSteinerTreeFinder createRdpQuasiExact(
			ConstraintsComparer constraintsComparer);
	
	ConstrainedSteinerTreeFinder createRdpHeuristic(
			ConstraintsComparer constraintsComparer);

	ConstrainedSteinerTreeFinder createHmcmc(
			ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
