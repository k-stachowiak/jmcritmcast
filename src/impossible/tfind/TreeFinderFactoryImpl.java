package impossible.tfind;

import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.PathFinder;
import impossible.tfind.paggr.PathAggrTreeFinder;
import impossible.tfind.prim.PrimTreeFinder;

import java.util.List;


public class TreeFinderFactoryImpl implements TreeFinderFactory {

	@Override
	public SteinerTreeFinder createPathAggr(List<Double> constraints,
			PathFinder pathFinder, SpanningTreeFinder spanningTreeFinder) {

		return new PathAggrTreeFinder(constraints, pathFinder,
				spanningTreeFinder);
	}

	@Override
	public SpanningTreeFinder createPrim(MetricProvider metricProvider) {
		return new PrimTreeFinder(metricProvider);
	}

}
