package edu.put.et.stik.mm.tfind;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.helpers.PathAggregator;
import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;

public interface TreeFinderFactory {
	MetricConstrainedSteinerTreeFinder createConstrainedPathAggr(
			ConstrainedPathFinder pathFinder, PathAggregator pathAggregator);

	MetricConstrainedSteinerTreeFinder createRdpQuasiExact(
			ConstraintsComparer constraintsComparer);
	
	MetricConstrainedSteinerTreeFinder createRdpHeuristic(
			ConstraintsComparer constraintsComparer);

	MetricConstrainedSteinerTreeFinder createHmcmc(
			ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
