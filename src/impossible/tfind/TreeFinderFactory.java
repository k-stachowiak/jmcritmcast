package impossible.tfind;

import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.PathFinder;

import java.util.List;


public interface TreeFinderFactory {
	SteinerTreeFinder createPathAggr(List<Double> constraints,
			PathFinder pathFinder, SpanningTreeFinder spanningTreeFinder);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
