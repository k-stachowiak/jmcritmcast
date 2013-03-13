package edu.put.et.stik.mm.tfind;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.helpers.PathAggregator;
import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;
import edu.put.et.stik.mm.tfind.hmcmc.HmcmcTreeFinder;
import edu.put.et.stik.mm.tfind.paggr.ConstrainedPathAggrTreeFinder;
import edu.put.et.stik.mm.tfind.prim.PrimTreeFinder;
import edu.put.et.stik.mm.tfind.rdp.RdpQuasiExact;

public class TreeFinderFactoryImpl implements TreeFinderFactory {

	@Override
	public MetricConstrainedSteinerTreeFinder createConstrainedPathAggr(
			ConstrainedPathFinder pathFinder, PathAggregator pathAggregator) {

		return new ConstrainedPathAggrTreeFinder(pathFinder, pathAggregator);
	}

	@Override
	public MetricConstrainedSteinerTreeFinder createRdpQuasiExact(
			ConstraintsComparer constraintsComparer) {
		return new RdpQuasiExact(constraintsComparer);
	}
	
	@Override
	public MetricConstrainedSteinerTreeFinder createRdpHeuristic(
			ConstraintsComparer constraintsComparer) {
		return new RdpQuasiExact(constraintsComparer);
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
