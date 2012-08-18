package edu.ppt.impossible.tfind;

import java.util.List;

import edu.ppt.impossible.helpers.metrprov.MetricProvider;
import edu.ppt.impossible.pfnd.PathFinder;

public interface TreeFinderFactory {
	SteinerTreeFinder createPathAggr(List<Double> constraints,
			PathFinder pathFinder, SpanningTreeFinder spanningTreeFinder);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
