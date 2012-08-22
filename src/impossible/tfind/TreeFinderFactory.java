package impossible.tfind;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.PathFinder;

import java.util.List;

public interface TreeFinderFactory {
	SteinerTreeFinder createPathAggr(List<Double> constraints,
			PathFinder pathFinder, ConstraintsComparer constraintsComparer,
			PathAggregator pathAggregator);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
