package edu.put.et.stik.mm.tfind;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.helpers.PathAggregator;
import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;

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
