package edu.ppt.impossible.tfind;

import java.util.List;

import edu.ppt.impossible.helpers.MetricProvider;
import edu.ppt.impossible.pfnd.PathFinder;

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
